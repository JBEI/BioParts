import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainComponent} from "./components/main/main.component";
import {SearchComponent} from "./components/search/search.component";
import {AnnotateComponent} from "./components/annotate/annotate.component";
import {PartDetailsComponent} from "./components/parts/part-details/part-details.component";

const routes: Routes = [
    { path: '', component: MainComponent },
    { path: 'search', component: SearchComponent },
    { path: 'annotate', component: AnnotateComponent },
    {path: 'part/:partId', component: PartDetailsComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
