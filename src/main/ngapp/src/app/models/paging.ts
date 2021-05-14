export class Paging {
    currentPage: number;
    available: number;
    asc: boolean;
    retrieveCount: number;
    start: number;
    index?: number;
    filterText: string;
    processing?: boolean;
    type?: string;
    sort: string;
    pageSize: number;

    sortField: string;
    sortAscending: boolean;

    constructor() {
        this.currentPage = 1;
        this.available = 0;         // number of items available
        this.retrieveCount = 30;    // maximum number of items to retrieve
        this.start = 0;
        this.filterText = '';
        this.processing = false;
        this.sort = 'id';
        this.asc = false;
        this.pageSize = 15;         // number of items per page
    }
}
