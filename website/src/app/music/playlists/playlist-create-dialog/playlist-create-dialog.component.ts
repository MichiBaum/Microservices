import {ChangeDetectionStrategy, Component, inject, model, output, signal} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Dialog} from 'primeng/dialog';
import {Button} from 'primeng/button';
import {InputText} from 'primeng/inputtext';
import {Textarea} from 'primeng/textarea';
import {FloatLabel} from 'primeng/floatlabel';
import {TranslatePipe} from '@ngx-translate/core';
import {MusicService} from '../../../core/api-services/music.service';
import {UserInfoService} from '../../../core/services/user-info.service';
import {Playlist} from '../../../core/models/music/playlist.model';

@Component({
  selector: 'app-playlist-create-dialog',
  imports: [
    Dialog,
    Button,
    InputText,
    Textarea,
    FloatLabel,
    ReactiveFormsModule,
    TranslatePipe
  ],
  templateUrl: './playlist-create-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PlaylistCreateDialogComponent {
  private readonly musicService = inject(MusicService);
  private readonly userInfoService = inject(UserInfoService);

  readonly visible = model<boolean>(false);
  readonly created = output<Playlist>();
  readonly submitting = signal<boolean>(false);

  readonly form = new FormGroup({
    name: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(255)]
    }),
    description: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.maxLength(2000)]
    })
  });

  submit(): void {
    if (this.form.invalid || this.submitting()) {
      return;
    }

    const rawName = this.form.controls.name.value.trim();
    if (!rawName) {
      return;
    }

    const rawDescription = this.form.controls.description.value.trim();

    this.submitting.set(true);
    this.musicService.createPlaylist({
      name: rawName,
      description: rawDescription ? rawDescription : null
    }).subscribe({
      next: (playlist) => {
        this.submitting.set(false);
        this.userInfoService.info('Playlist created', `"${playlist.name}" was created successfully.`);
        this.created.emit(playlist);
        this.close();
      },
      error: () => {
        this.submitting.set(false);
        this.userInfoService.error('Error', 'Failed to create playlist. Please try again.');
      }
    });
  }

  close(): void {
    this.form.reset({name: '', description: ''});
    this.visible.set(false);
  }
}
