import {inject, Injectable} from "@angular/core";
import {Meta} from "@angular/platform-browser";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class MetaService {
  private readonly meta = inject(Meta)
  private readonly environment = inject(EnvironmentConfig)

  getKeywords(): string[] {
    const content = this.meta.getTag('keywords')?.content;
    return content ? content.split(',').map(keyword => keyword.trim()) : [];
  }

  addKeywords(keywords: string[]) {
    const existingKeywords = this.getKeywords();
    const updatedKeywords = Array.from(new Set([...existingKeywords, ...keywords]));
    this.meta.updateTag({name: 'keywords', content: updatedKeywords.join(',')});
  }

  removeKeywords(keywordsToRemove: string[]) {
    const existingKeywords = this.getKeywords();
    const updatedKeywords = existingKeywords.filter(
      keyword => !keywordsToRemove.includes(keyword)
    );
    this.meta.updateTag({name: 'keywords', content: updatedKeywords.join(',')});
  }

  getDescription(): string {
    return this.meta.getTag('description')?.content || "";
  }

  updateDescription(content: string): void {
    this.meta.updateTag({ name: 'description', content });
  }

  updateDescriptionAndReturnOld(newContent: string): string {
    const oldContent = this.getDescription();
    this.updateDescription(newContent);
    return oldContent;
  }

}
