import {ChangeDetectionStrategy, Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Button} from 'primeng/button';
import {Tag} from 'primeng/tag';
import {Skeleton} from 'primeng/skeleton';
import {Dialog} from 'primeng/dialog';
import {InputText} from 'primeng/inputtext';
import {Textarea} from 'primeng/textarea';
import {FloatLabel} from 'primeng/floatlabel';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faArrowLeft, faMusic, faPen, faRotate, faTrash} from '@fortawesome/free-solid-svg-icons';
import {faSpotify} from '@fortawesome/free-brands-svg-icons';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {PrimeTemplate} from 'primeng/api';
import {MusicService} from '../../../core/api-services/music.service';
import {UserInfoService} from '../../../core/services/user-info.service';
import {UserConfirmationService} from '../../../core/services/user-confirmation.service';
import {PlaylistDetails, PlaylistTrack} from '../../../core/models/music/playlist.model';

@Component({
  selector: 'app-playlist-detail',
  imports: [
    Button,
    Tag,
    Skeleton,
    Dialog,
    InputText,
    Textarea,
    FloatLabel,
    FaIconComponent,
    TranslatePipe,
    ReactiveFormsModule,
    RouterLink,
    PrimeTemplate
  ],
  templateUrl: './playlist-detail.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PlaylistDetailComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly musicService = inject(MusicService);
  private readonly userInfoService = inject(UserInfoService);
  private readonly userConfirmationService = inject(UserConfirmationService);
  private readonly translateService = inject(TranslateService);

  readonly playlist = signal<PlaylistDetails | null>(null);
  readonly loading = signal<boolean>(true);
  readonly syncing = signal<boolean>(false);
  readonly editDialogOpen = signal<boolean>(false);
  readonly submittingEdit = signal<boolean>(false);

  readonly faArrowLeft = faArrowLeft;
  readonly faMusic = faMusic;
  readonly faSpotify = faSpotify;
  readonly faRotate = faRotate;
  readonly faPen = faPen;
  readonly faTrash = faTrash;

  readonly editForm = new FormGroup({
    name: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(255)]
    }),
    description: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.maxLength(2000)]
    })
  });

  ngOnInit(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id');
    if (id) {
      this.loadPlaylist(id);
    } else {
      this.loading.set(false);
    }
  }

  loadPlaylist(id: string): void {
    this.loading.set(true);
    this.musicService.getPlaylistDetails(id).subscribe({
      next: (details) => {
        this.playlist.set(details);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.userInfoService.error('Error', 'Failed to load playlist details.');
      }
    });
  }

  resyncSpotify(): void {
    const current = this.playlist();
    if (!current || this.syncing()) {
      return;
    }

    this.syncing.set(true);
    this.musicService.syncPlaylistById(current.id).subscribe({
      next: (details) => {
        this.playlist.set(details);
        this.syncing.set(false);
        this.userInfoService.info('Re-sync Complete', 'Playlist updated from Spotify.');
      },
      error: () => {
        this.syncing.set(false);
        this.userInfoService.error('Re-sync Failed', 'Failed to re-sync playlist with Spotify.');
      }
    });
  }

  openEditDialog(): void {
    const current = this.playlist();
    if (!current) {
      return;
    }

    this.editForm.setValue({
      name: current.name,
      description: current.description ?? ''
    });
    this.editDialogOpen.set(true);
  }

  submitEdit(): void {
    const current = this.playlist();
    if (!current || this.editForm.invalid || this.submittingEdit()) {
      return;
    }

    const rawName = this.editForm.controls.name.value.trim();
    if (!rawName) {
      return;
    }

    const rawDescription = this.editForm.controls.description.value.trim();

    this.submittingEdit.set(true);
    this.musicService.updatePlaylist(current.id, {
      name: rawName,
      description: rawDescription ? rawDescription : null
    }).subscribe({
      next: (updatedDto) => {
        this.playlist.update((p) => p ? {
          ...p,
          name: updatedDto.name,
          description: updatedDto.description,
          coverImageUrl: updatedDto.coverImageUrl,
          updated: updatedDto.updated
        } : null);
        this.submittingEdit.set(false);
        this.editDialogOpen.set(false);
        this.userInfoService.info('Playlist Updated', `"${updatedDto.name}" was saved successfully.`);
      },
      error: () => {
        this.submittingEdit.set(false);
        this.userInfoService.error('Update Failed', 'Failed to update playlist.');
      }
    });
  }

  closeEditDialog(): void {
    this.editDialogOpen.set(false);
  }

  confirmDelete(): void {
    const current = this.playlist();
    if (!current) {
      return;
    }

    this.userConfirmationService.deleteConfirm({
      header: this.translateService.instant('music.playlist-detail.delete-confirm-title'),
      message: this.translateService.instant('music.playlist-detail.delete-confirm-message'),
      accept: () => {
        this.musicService.deletePlaylist(current.id).subscribe({
          next: () => {
            this.userInfoService.info('Playlist Deleted', `"${current.name}" was removed.`);
            this.router.navigate(['/music/library/playlists']);
          },
          error: () => {
            this.userInfoService.error('Delete Failed', 'Failed to delete playlist.');
          }
        });
      }
    });
  }

  formatArtists(track: PlaylistTrack): string {
    return track.artists.map((artist) => artist.name).join(', ');
  }
}
