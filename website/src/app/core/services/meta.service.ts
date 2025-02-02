import {inject, Injectable} from "@angular/core";
import {Meta} from "@angular/platform-browser";

@Injectable({providedIn: 'root'})
export class MetaService {
    defaultHolder: MetaDataHolder = {
        description: "A website about Chess, Fitness, Music and more. Combining many services into one.",
        keywords: ["Chess", "Fitness", "Music", "Michael Baumberger", "MichiBaum"]
    };
    private readonly meta = inject(Meta)

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

    getKeywords(): string[] {
        const content = this.meta.getTag('name=keywords')?.content;
        return content ? content.split(',').map(keyword => keyword.trim()) : [];
    }

    setKeyWords(keywords: string[]) {
        const oldKeywords = this.getKeywords();
        this.removeKeywords(oldKeywords);
        this.addKeywords(keywords);
    }

    getDescription(): string {
        return this.meta.getTag('name=description')?.content || "";
    }

    updateDescription(content: string): void {
        this.meta.updateTag({name: 'description', content});
    }

    updateDescriptionAndReturnOld(newContent: string): string {
        const oldContent = this.getDescription();
        this.updateDescription(newContent);
        return oldContent;
    }

    updateKeyWordsAndReturnOld(newContent: string[]): string[] {
        const oldContent = this.getKeywords();
        this.setKeyWords(newContent);
        return oldContent;
    }

    setNewAndGetOld(newMetaData: MetaDataHolder): MetaDataHolder {
        const oldDescription = this.updateDescriptionAndReturnOld(newMetaData.description)
        const oldKeywords = this.updateKeyWordsAndReturnOld(newMetaData.keywords)
        return {description: oldDescription, keywords: oldKeywords}
    }

}

export interface MetaDataHolder {
    description: string;
    keywords: string[];
}
