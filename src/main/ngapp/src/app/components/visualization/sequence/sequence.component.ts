import {Component, Input, OnInit} from '@angular/core';
import {Sequence} from '../../../models/Sequence';
import {VectorEditorService} from "./vector-editor.service";
import {Part} from "../../../models/Part";
import {HttpService} from "../../../services/http.service";
import {PartWithSequence} from "../../../models/part-with-sequence";

@Component({
    selector: 'app-sequence',
    templateUrl: './sequence.component.html',
    styleUrls: ['./sequence.component.css']
})

export class SequenceComponent implements OnInit {

    data: any;
    editor: any;
    @Input() part: Part;
    @Input() sequence: Sequence;

    constructor(private vectorEditor: VectorEditorService, private http: HttpService) {
    }

    ngOnInit(): void {
        if (this.part) {
            this.http.get('search/' + this.part.recordId).subscribe((result: PartWithSequence) => {
                console.log(result);
                if (!result.sequence.name)
                    result.sequence.name = this.part.name;
                this.showSequenceVisualization(result.sequence)
            })
        } else {
            this.showSequenceVisualization(this.sequence);
        }
    }

    showSequenceVisualization(sequence): void {
        this.data = {
            sequenceData: {
                sequence: sequence.sequence,
                features: this.vectorEditor.convertFeaturesToTSModel(sequence.features),
                name: sequence.name,
                circular: true,
            }
        };

        console.log(this.data);

        let root = 'preview-root';
        this.editor = (window as any).createVectorEditor(document.getElementById(root), {
            readOnly: true,
            doNotUseAbsolutePosition: true,
            PropertiesProps: {propertiesList: this.vectorEditor.propertiesList()},
            ToolBarProps: {toolList: this.vectorEditor.toolList()}
        });

        this.editor.updateEditor({
            readOnly: true,
            sequenceData: this.data.sequenceData,
            annotationVisibility: {parts: false, orfs: false, cutsites: false, translations: false},
            panelsShown: this.vectorEditor.panelsList(),
            selectionLayer: {
                start: -1,
                end: -1
            },
        });
    }
}
