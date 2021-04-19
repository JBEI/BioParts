import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {HttpService} from "../../services/http.service";
import {SearchService} from "../../services/search.service";
import {SearchQuery} from "../../models/search-query";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    active = 1;
    query: SearchQuery;

    constructor(private router: Router, private http: HttpService, private search: SearchService) {
        this.query = new SearchQuery();
    }

    ngOnInit(): void {
    }

    runSearch(): void {
        this.search.setQuery(this.query);
        this.router.navigate(['search']);
    }

    runAutoAnnotate(): void {
        this.search.setQuery(this.query);
        this.router.navigate(['annotate']);
    }

    resetBlastInput(): void {
        this.query.blastQuery.sequence = '';
        this.query.blastQuery.blastProgram = 'BLAST_N';
    }

    showAddRegistryForm(): void {
    }
}
