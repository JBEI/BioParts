import {Component, OnInit} from '@angular/core';
import {HttpService} from "../../services/http.service";
import {SearchService} from "../../services/search.service";
import {SearchResults} from "../../models/search-results";
import {SearchQuery} from "../../models/search-query";
import {Paging} from "../../models/paging";

@Component({
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

    hStepOptions = [15, 30, 50, 100];
    query: SearchQuery;
    searchResults: SearchResults;
    loadingSearchResults: boolean;
    currentTooltip: any;
    paging: Paging;

    constructor(private http: HttpService, private searchService: SearchService) {
        this.paging = new Paging();
    }

    ngOnInit(): void {
        this.query = this.searchService.getQuery();

        if (!this.query.queryString || !this.query.blastQuery.sequence) {
            this.query = new SearchQuery();
            this.searchResults = new SearchResults();

            // TODO : remove
            this.runAdvancedSearch();
            // TODO
        } else {
            this.paging.offset = 0;
            if (!this.query.blastQuery.blastProgram)
                this.query.blastQuery.blastProgram = "BLAST_N";
            this.runAdvancedSearch();
        }
    }

    sortResults(field: string): void {
        this.paging.sortField = field;
        this.paging.offset = 0;
    }

    runAdvancedSearch(): void {
        this.query = this.searchService.getQuery();

        if (!this.query.queryString && !this.query.blastQuery.sequence) {
            this.searchResults = new SearchResults();
            return;
        }

        this.loadingSearchResults = true;

        // reset search results
        this.searchResults = undefined;

        // run search
        this.http.post("search", this.query).subscribe((result) => {
            if (result) {
                this.searchResults = result;
                this.paging.available = result.resultCount;
                this.loadingSearchResults = false;
            }
        }, (error) => {
            console.error(error);
            this.loadingSearchResults = false;
        });
    };

    setSelected(index): void {
        this.paging.offset = index;
        this.searchService.setQuery(this.query);
    };

    hStepChanged(): void {
        this.paging.offset = 0;
        this.paging.currentPage = 1;
        this.runAdvancedSearch();
    };

    searchResultPageChanged(): void {
        this.paging.offset = ((this.paging.currentPage - 1) * this.paging.available) + 1;
        this.runAdvancedSearch();
    };

    getType(relScore: number): string {
        if (relScore === undefined)
            return 'info';

        if (relScore >= 70)
            return 'success';
        if (relScore >= 30 && relScore < 70)
            return 'warning';
        if (relScore < 30)
            return 'danger';
        return 'info';
    };

    tooltipDetails(entry): void {
        this.currentTooltip = undefined;
        this.http.get("parts/" + entry.id + "/tooltip").subscribe((result) => {
            this.currentTooltip = result;
        });
    };

    pageCounts(maxPageCount = 30): string {
        const currentPage = this.paging.currentPage;
        const resultCount = this.paging.available;

        const pageNum = ((currentPage - 1) * maxPageCount) + 1;

        // number on this page
        const pageCount = (currentPage * maxPageCount) > resultCount ? resultCount : (currentPage * maxPageCount);
        return (pageNum) + " - " + (pageCount) + " of " + (resultCount);
    };
}
