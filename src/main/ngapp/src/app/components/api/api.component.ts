import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
    selector: 'app-api',
    templateUrl: './api.component.html',
    styleUrls: ['./api.component.css']
})
export class ApiComponent implements OnInit {

    selection: string;

    constructor(private router: Router) {
        this.selection = 'annotations';
    }

    ngOnInit(): void {
    }

    goHome(): void {
        this.router.navigate(['/']);
    }

    select(selection: string): void {
        this.selection = selection;
    }

    scroll(id: string) {
        const el = document.getElementById(id);
        //
        el.scrollIntoView();
    }
}
