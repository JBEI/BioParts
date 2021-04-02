import {SearchQuery} from "./search-query";
import {SearchResult} from "./search-result";

export class SearchResults {
    resultCount: number;
    results: SearchResult[];
    query?: SearchQuery;

    constructor() {
        this.results = [];
    }
}
