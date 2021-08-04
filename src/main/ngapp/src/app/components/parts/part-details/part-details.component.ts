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
    retrievingPart: boolean;
    showComments: boolean;
    showExperiments: boolean;
    showAttachments: boolean;
    query: SearchQuery;

    constructor(private http: HttpService, private route: ActivatedRoute, private searchService: SearchService) {
    }

    ngOnInit(): void {
        this.retrievingPart = true;
        this.http.get('search/' + this.route.snapshot.params['partId']).subscribe((data: PartWithSequence) => {
            this.partSequence = data;
            this.route.params.subscribe(result => {
                this.partSequence.part.recordId = result.partId;
            })
            this.retrievingPart = false;
        }, (error => {
            console.error(error);
            this.retrievingPart = false;
        }));

        // this.route.params.subscribe((params) => {
        //     console.log('partId', params['partId'])
        // });

        this.selection = 'general';
        this.showComments = false;
        this.showExperiments = false;
        this.showAttachments = false;
        this.query = this.searchService.getQuery();
    }

    showSideBar(comments: boolean, experiments: boolean, attachments: boolean): void {
        this.showComments = false;
        this.showExperiments = false;
        this.showAttachments = false;

        if (comments)
            this.showComments = true;
        else if (experiments)
            this.showExperiments = true;
        else if (attachments)
            this.showAttachments = true;
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
