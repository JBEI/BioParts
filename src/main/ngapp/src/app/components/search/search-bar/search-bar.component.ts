import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Router} from "@angular/router";
import {SearchService} from "../../../services/search.service";
import {SearchQuery} from "../../../models/search-query";

@Component({
    selector: 'app-search-bar',
    templateUrl: './search-bar.component.html',
    styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {

    query: SearchQuery;
    @Output() runSearch: EventEmitter<any> = new EventEmitter<any>();

    constructor(private search: SearchService, private router: Router) {
    }

    ngOnInit(): void {
        this.query = this.search.getQuery();
    }

    submitSearch(): void {
        this.search.setQuery(this.query);
        this.router.navigate(['search']);
        this.runSearch.emit();
    }
}
