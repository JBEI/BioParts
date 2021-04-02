import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PartWithSequence} from "../../../../models/part-with-sequence";

@Component({
    selector: 'app-details',
    templateUrl: './details.component.html',
    styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {

    part: PartWithSequence;

    constructor(private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.data.subscribe((data) => {
            console.log(data);
            this.part = data.part;
        })
    }
}
