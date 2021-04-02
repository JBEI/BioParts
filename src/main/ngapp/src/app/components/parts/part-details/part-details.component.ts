import {Component, OnInit} from '@angular/core';
import {HttpService} from "../../../services/http.service";
import {ActivatedRoute, ActivatedRouteSnapshot} from "@angular/router";
import {PartWithSequence} from "../../../models/part-with-sequence";

@Component({
    selector: 'app-part-details',
    templateUrl: './part-details.component.html',
    styleUrls: ['./part-details.component.css']
})
export class PartDetailsComponent implements OnInit {

    partSequence: PartWithSequence;
    context: any;
    selection: string;
    active: number;
    details: boolean;

    constructor(private http: HttpService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.data.subscribe((data) => {
            this.partSequence = data.partSequence;
            this.route.params.subscribe(result => {
                this.partSequence.part.recordId = result.partId;
            })
        });

        this.selection = 'general';
        this.details = false;
    }

    showDetails(): void {
        this.details = true;
    }

    backTo(): void {

    }

    nextEntryInContext(): void {

    }

    prevEntryInContext(): void {

    }

    restoreSelectedEntries(): void {

    }

    submitSelectedImportEntry(): void {
    }
}
