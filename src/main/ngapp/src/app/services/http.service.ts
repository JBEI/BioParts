import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable, of} from "rxjs";
import {catchError} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class HttpService {

    private readonly apiUrl: string;

    private httpOptions = {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        params: new HttpParams()
    };

    constructor(private http: HttpClient) {
        this.apiUrl = environment.apiUrl;
    }

    get<T>(api: string, options?): Observable<T> {
        this.setOptions(options);

        const url = `${this.apiUrl}/${api}`;
        return this.http.get<T>(url, this.httpOptions)
            .pipe(
                catchError(this.handleError<T>())
            );
    }

    post<T>(api: string, payload: T, options?): Observable<any> {
        this.setOptions(options);
        const url = `${this.apiUrl}/${api}`;
        return this.http.post<T>(url, payload);
    }

    delete<T>(api: string): Observable<any> {
        const url = `${this.apiUrl}/${api}`;
        return this.http.delete(url, this.httpOptions);
    }

    put<T>(api: string, payload: T, options?): Observable<any> {
        this.setOptions(options);
        const url = `${this.apiUrl}/${api}`;
        return this.http.put(url, payload, this.httpOptions);
    }

    private setOptions(options): void {
        this.httpOptions.params = new HttpParams();
        if (!options) {
            return;
        }

        for (const prop in options) {
            if (!options.hasOwnProperty(prop)) {
                continue;
            }

            this.httpOptions.params = this.httpOptions.params.append(prop, options[prop]);
        }
    }

    private handleError<T>(result?) {
        return (error: any): Observable<T> => {
            console.error(error); // log to console instead

            // Let the app keep running by returning an empty result.
            return of(result as T);
        };
    }
}
