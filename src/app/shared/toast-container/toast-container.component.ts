import { Component, TemplateRef, Input } from '@angular/core';
import { ToastService } from '../toast.service';

@Component({
  selector: 'pkr-toast-container',
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.scss'],
  host: {'[class.ngb-toasts]': 'true'}
})
export class ToastContainerComponent {
  @Input('header') header: string;

  constructor(public toastService: ToastService) { }

  public isTemplate(toast: any) {
    return toast.textOrTpl instanceof TemplateRef;
  }
}
