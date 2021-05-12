import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MainComponent} from './components/main/main.component';
import {SearchComponent} from './components/search/search.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {NgbDropdownModule, NgbNavModule, NgbPaginationModule, NgbTooltipModule} from "@ng-bootstrap/ng-bootstrap";
import {TextResultsComponent} from './components/search/text-results/text-results.component';
import {AnnotateComponent} from './components/annotate/annotate.component';
import {HeaderComponent} from "./components/header/header.component";
import {SearchBarComponent} from "./components/search/search-bar/search-bar.component";
import {PartDetailsComponent} from "./components/parts/part-details/part-details.component";
import {PartGeneralInformationComponent} from "./components/parts/part-general-information/part-general-information.component";
import {SequenceComponent} from "./components/visualization/sequence/sequence.component";
import {OptionsFieldComponent} from "./components/parts/widgets/options-field/options-field.component";
import {WithEmailFieldComponent} from "./components/parts/widgets/with-email-field/with-email-field.component";
import {AutoCompleteFieldComponent} from "./components/parts/widgets/auto-complete-field/auto-complete-field.component";
import {LoginComponent} from "./components/login/login.component";
import {CommentComponent} from './components/parts/part-details/comment/comment.component';
import {ExperimentsComponent} from './components/parts/part-details/experiments/experiments.component';
import {AttachmentsComponent} from './components/parts/part-details/attachments/attachments.component';

@NgModule({
    declarations: [
        AppComponent,
        MainComponent,
        SearchComponent,
        HeaderComponent,
        TextResultsComponent,
        SequenceComponent,
        AnnotateComponent,
        HeaderComponent,
        SearchBarComponent,
        PartDetailsComponent,
        PartGeneralInformationComponent,
        OptionsFieldComponent,
        WithEmailFieldComponent,
        AutoCompleteFieldComponent,
        LoginComponent,
        CommentComponent,
        ExperimentsComponent,
        AttachmentsComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbNavModule,
        FormsModule,
        HttpClientModule,
        NgbDropdownModule,
        NgbTooltipModule,
        NgbPaginationModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
