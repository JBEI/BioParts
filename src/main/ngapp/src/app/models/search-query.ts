import {BlastQuery} from "./blast-query";
import {Paging} from "./paging";

export class SearchQuery {
    queryString: string;
    annotationSource: string;
    blastQuery: BlastQuery;
    parameters: Paging;

    constructor() {
        this.queryString = '';
        this.blastQuery = new BlastQuery();
        this.parameters = new Paging();
        this.annotationSource = 'ALL';
    }
}
