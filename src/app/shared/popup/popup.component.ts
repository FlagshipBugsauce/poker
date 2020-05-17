import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {NgbActiveModal, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'pkr-popup',
  templateUrl: './popup.component.html',
  styleUrls: ['./popup.component.scss']
})
export class PopupComponent implements OnInit {
  /** Specify the title of the popup. */
  @Input() title: string;

  /** Specify the desired size of the popup. */
  @Input() size: string = 'md';

  /** Specify whether the popup should be centered vertically. */
  @Input() centered: boolean = true;

  /** Specify what content should appear in the popup. */
  @Input() content: PopupContentModel[];

  /** Specify whether the popup should have a cancel button. */
  @Input() cancelButton: boolean = true;

  /** Specify a function that will be called when the popup is closed by clicking Ok. */
  @Input() okCloseProcedure: () => void;

  /** Specify a function that will be called when the popup is closed by clicking Cancel or the X in the top-right. */
  @Input() cancelCloseProcedure: () => void;

  /** Reference to the popup. Needed so that this popup and ONLY this popup can be closed. */
  @ViewChild('popup') popup: NgbActiveModal;
  private ngbModalRef: NgbModalRef;

  constructor(private ngbModal: NgbModal) { }

  public ngOnInit(): void {
  }

  /**
   * Creates a popup on the screen.
   */
  public open(): void {
    this.ngbModalRef = this.ngbModal.open(this.popup, {
      size: this.size,
      centered: this.centered
    });
  }

  /**
   * Removes the popup from the screen when cancel, or the X in the top-right, is clicked.
   */
  public cancelClose(): void {
    if (this.cancelCloseProcedure != null) {
      this.cancelCloseProcedure();
    }
    this.ngbModalRef.close();
  }

  /**
   * Removes the popup from the screen when Ok is clicked.
   */
  public okClose(): void {
    if (this.okCloseProcedure != null) {
      this.okCloseProcedure();
    }
    this.ngbModalRef.close();
  }
}

/**
 * A section of content on the popup. Can have a header if desired, or a body.
 * The header will be in an <h3> tag, the body in a <p> tag.
 */
export interface PopupContentModel {
  /** Specify a title for a section of content on the modal. */
  header?: string;
  body: string;
}
