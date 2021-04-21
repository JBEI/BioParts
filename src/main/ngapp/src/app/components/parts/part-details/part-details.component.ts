import {Component, OnInit} from '@angular/core';
import {HttpService} from "../../../services/http.service";
import {ActivatedRoute} from "@angular/router";
import {PartWithSequence} from "../../../models/part-with-sequence";
import {SearchQuery} from "../../../models/search-query";
import {SearchService} from "../../../services/search.service";

@Component({
    selector: 'app-part-details',
    templateUrl: './part-details.component.html',
    styleUrls: ['./part-details.component.css']
})
export class PartDetailsComponent implements OnInit {

    partSequence: PartWithSequence;
    selection: string;
    active: number;
    details: boolean;
    query: SearchQuery;

    constructor(private http: HttpService, private route: ActivatedRoute, private searchService: SearchService) {
    }

    ngOnInit(): void {
        this.http.get('search/' + this.route.snapshot.params['partId']).subscribe((data: PartWithSequence) => {
            this.partSequence = data;
            this.route.params.subscribe(result => {
                this.partSequence.part.recordId = result.partId;
            })
        });

        // this.route.params.subscribe((params) => {
        //     console.log('partId', params['partId'])
        // });

        this.selection = 'general';
        this.details = false;
        this.query = this.searchService.getQuery();
    }

    showDetails(): void {
        this.details = true;
    }

    backTo(): void {
    }

    nextEntryInContext(): void {
        this.query.parameters.retrieveCount = 1;
        this.query.parameters.start += 1;
        this.http.post("search", this.query).subscribe((result) => {
            if (!result || !result.results) {
                return;
            }

            this.http.get('search/' + result.results[0].entryInfo.recordId).subscribe((p: PartWithSequence) => {
                this.partSequence = p;
                console.log(p);
            })
            console.log(result);
        });
    }

    prevEntryInContext(): void {
        this.query.parameters.retrieveCount = 1;

        this.query.parameters.start -= 1;
        this.http.post("search", this.query).subscribe((result) => {
            if (!result || !result.results) {
                return;
            }

            this.http.get('search/' + result.results[0].entryInfo.recordId).subscribe((p: PartWithSequence) => {
                this.partSequence = p;
                console.log(p);
            })
        });
    }
}
