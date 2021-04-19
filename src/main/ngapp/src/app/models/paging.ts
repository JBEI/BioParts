export class Paging {
    currentPage: number;
    available: number;
    asc: boolean;
    limit: number;
    offset: number;
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
        this.available = 0;
        this.limit = 15;
        this.offset = 0;
        this.filterText = '';
        this.processing = false;
        this.sort = 'id';
        this.asc = false;
        this.pageSize = 5;
    }
}