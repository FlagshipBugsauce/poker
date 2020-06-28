import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {AppStateContainer} from './shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectJwt} from './state/app.selector';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
  private jwt: string = '';
  private bearer: string = 'Bearer ';

  constructor(private appStore: Store<AppStateContainer>) {
    this.appStore.select(selectJwt).subscribe(jwt => this.jwt = jwt);
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Apply the headers
    req = req.clone({
      setHeaders: {
        Authorization: this.bearer + this.jwt
      }
    });

    // Also handle errors globally
    return next.handle(req).pipe(
      tap(x => x, err => {
        // Handle this err
        console.error(`Error performing request, status code = ${err.status}`);
      })
    );
  }
}
