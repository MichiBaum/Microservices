import {Component, inject} from '@angular/core';
import {Fieldset} from "primeng/fieldset";
import {FileUpload} from "primeng/fileupload";
import {EnvironmentConfig} from "../../core/config/environment.config";
import {Button} from "primeng/button";

@Component({
  selector: 'app-fide-import',
  imports: [
    Fieldset,
    FileUpload,
    Button
  ],
  templateUrl: './fide-import.component.html',
  styleUrl: './fide-import.component.scss'
})
export class FideImportComponent {
  private readonly environment = inject(EnvironmentConfig);
  protected readonly chessBaseUrl = this.environment.chessService();

}
