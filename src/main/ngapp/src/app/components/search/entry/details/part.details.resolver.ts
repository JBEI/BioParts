import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {HttpService} from "../../../../services/http.service";
import {Observable} from "rxjs";
import {PartWithSequence} from "../../../../models/part-with-sequence";

@Injectable()
export class PartDetailsResolver implements Resolve<PartWithSequence> {

    constructor(private http: HttpService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<PartWithSequence> {
        return this.http.get('search/' + route.params.partId);
    }
}
