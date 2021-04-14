import {Component, OnInit} from '@angular/core';
import {HttpService} from "../../services/http.service";
import {SearchService} from "../../services/search.service";
import {SearchResults} from "../../models/search-results";
import {SearchQuery} from "../../models/search-query";

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

    constructor(private http: HttpService, private searchService: SearchService) {
    }

    ngOnInit(): void {
        this.query = this.searchService.getQuery();

        if (!this.query) {
            this.query = new SearchQuery();
            this.searchResults = new SearchResults();
        } else {
            this.query.parameters.offset = 0;
            if (!this.query.blastQuery.blastProgram)
                this.query.blastQuery.blastProgram = "BLAST_N";

            this.runAdvancedSearch();
        }
    }

    sortResults(field: string): void {
        this.query.parameters.sortField = field;
        this.query.parameters.offset = 0;
    }

    runAdvancedSearch(): void {
        this.query = this.searchService.getQuery();

        if (!this.query.queryString) {
            this.searchResults = new SearchResults();
            return;
        }

        this.loadingSearchResults = true;
        this.http.post("search", this.query).subscribe((result) => {
            if (result) {
                this.searchResults = result;
                this.query.parameters.available = result.resultCount;
                this.loadingSearchResults = false;
            }
        }, (error) => {
            console.error(error);
            this.loadingSearchResults = false;
            this.searchResults = undefined;
        });
    };

    setSelected(index): void {
        this.query.parameters.offset = index;
        this.searchService.setQuery(this.query);
    };

    hStepChanged(): void {
        this.query.parameters.offset = 0;
        this.query.parameters.currentPage = 1;
        this.runAdvancedSearch();
    };

    searchResultPageChanged(): void {
        this.query.parameters.offset = ((this.query.parameters.currentPage - 1) * this.query.parameters.available) + 1;
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

    pageCounts(currentPage, resultCount, maxPageCount): string {
        if (!maxPageCount)
            maxPageCount = 30;
        const pageNum = ((currentPage - 1) * maxPageCount) + 1;

        // number on this page
        const pageCount = (currentPage * maxPageCount) > resultCount ? resultCount : (currentPage * maxPageCount);
        return (pageNum) + " - " + (pageCount) + " of " + (resultCount);
    };
}
