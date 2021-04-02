import {Component, OnInit} from '@angular/core';
import {SearchService} from "../../services/search.service";
import {SearchQuery} from "../../models/search-query";
import {HttpService} from "../../services/http.service";
import {Sequence} from "../../models/sequence";

@Component({
    selector: 'app-annotate',
    templateUrl: './annotate.component.html',
    styleUrls: ['./annotate.component.css']
})
export class AnnotateComponent implements OnInit {

    private query: SearchQuery;
    features: any[];
    sequence: Sequence;
    selectedFeatures: any[];

    constructor(private searchService: SearchService, private http: HttpService) {
    }

    ngOnInit(): void {
        this.query = this.searchService.getQuery();
        this.sequence = new Sequence();
        if (this.query)
            this.sequence.sequence = this.query.blastQuery.sequence;
        this.selectedFeatures = [];

        // fetch matching annotations
        this.http.post('annotations', this.query.blastQuery).subscribe((result: any) => {
            this.sequence.sequence = this.query.blastQuery.sequence;
            this.features = result;
        })
    }

    isSelected(feature: any): boolean {
        return this.selectedFeatures.indexOf(feature) != -1;
    }

    selectFeature(feature: any): void {
        const idx = this.selectedFeatures.indexOf(feature);
        if (idx !== -1) {
            this.selectedFeatures.splice(idx, 1);
        } else {
            this.selectedFeatures.push(feature);
        }

        // this is to trigger the onChange on the visualization component
        this.selectedFeatures = Object.assign([], this.selectedFeatures);
        // todo : add to features
    }
}
