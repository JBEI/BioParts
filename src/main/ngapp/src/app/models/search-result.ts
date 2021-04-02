import {RegistrySource} from "./registry-source";
import {Part} from "./part";

export class SearchResult {
    eValue: string;
    alignment: string;
    queryLength: number;
    nident: number;
    score: number;
    maxScore: number;
    matchDetails?: String[];
    partner: RegistrySource;
    entryInfo: Part;
}
