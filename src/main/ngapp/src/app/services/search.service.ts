import {Injectable} from '@angular/core';
import {SearchQuery} from "../models/search-query";

@Injectable({
    providedIn: 'root'
})
export class SearchService {

    search: SearchQuery;

    constructor() {
        this.search = new SearchQuery();
    }

    setQuery(search: SearchQuery) {
        this.search = search;
    }

    getQuery(): SearchQuery {
        return this.search;
    }
}
