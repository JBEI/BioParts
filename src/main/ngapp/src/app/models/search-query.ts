import {BlastQuery} from "./blast-query";

export class SearchQuery {
    queryString: string;
    blastQuery: BlastQuery;

    constructor() {
        this.queryString = 'test';
        this.blastQuery = new BlastQuery();
    }
}
