import {Component, OnInit} from '@angular/core';
import {SearchService} from "../../services/search.service";
import {SearchQuery} from "../../models/search-query";
import {HttpService} from "../../services/http.service";
import {Sequence} from "../../models/Sequence";
import {VectorEditorService} from "../visualization/sequence/vector-editor.service";

@Component({
    selector: 'app-annotate',
    templateUrl: './annotate.component.html',
    styleUrls: ['./annotate.component.css']
})
export class AnnotateComponent implements OnInit {

    private query: SearchQuery;
    features: any[];
    sequence: Sequence;
    selectedFeatures: any[];
    data: any;
    editor: any;

    constructor(private vectorEditor: VectorEditorService, private searchService: SearchService, private http: HttpService) {
    }

    ngOnInit(): void {
        this.query = this.searchService.getQuery();
        this.sequence = new Sequence();
        if (this.query)
            this.sequence.sequence = this.query.blastQuery.sequence;

        this.selectedFeatures = [];

        // fetch matching annotations
        this.http.post('annotations', this.query.blastQuery).subscribe((result: any) => {
            this.sequence.sequence = this.query.blastQuery.sequence;
            this.features = result;
        })

        this.showSequenceVisualization();
    }

    showSequenceVisualization(): void {
        this.data = {
            sequenceData: {
                sequence: this.sequence.sequence,
                features: this.vectorEditor.convertFeaturesToTSModel(this.sequence.features),
                name: this.sequence.name ? this.sequence.name : '',
                circular: true,
            }
        };

        let root = 'annotation-preview-root';
        this.editor = (window as any).createVectorEditor(document.getElementById(root), {
            readOnly: true,
            doNotUseAbsolutePosition: true,
            showMenuBar: true,
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

    isSelected(feature: any): boolean {
        return this.selectedFeatures.indexOf(feature) != -1;
    }

    selectFeature(feature: any): void {
        const idx = this.selectedFeatures.indexOf(feature);
        if (idx !== -1) {
            this.selectedFeatures.splice(idx, 1);
        } else {
            this.selectedFeatures.push(feature);
        }

        // this is to trigger the onChange on the visualization component
        this.selectedFeatures = Object.assign([], this.selectedFeatures);
        this.data.sequenceData.features = this.vectorEditor.convertFeaturesToTSModel(this.selectedFeatures);
        this.editor.updateEditor({
            sequenceData: this.data.sequenceData,
        });
        // gtcacactggctcaccttcgggtgggcctttctgcgtttatatccctatcagtgatagagattgacatccctatcagtgatagagatactgagcactactagagtcacacaggaaagtactagatggtgagcaagggcgaggagctgttcaccggggtggtgcccatcctggtcgagctggacggcgacgtaaacggccacaagttcagcgtgtccggcgagggcgagggcgatgccacctacggcaagctgaccctgaagttcatctgcaccaccggcaagctgcccgtgccctggcccaccctcgtgaccaccttcggctacggcctgcaatgcttcgcccgctaccccgaccacatgaagctgcacgacttcttcaagtccgccatgcccgaaggctacgtccaggagcgcaccatcttcttcaaggacgacggcaactacaagacccgcgccgaggtgaagttcgagggcgacaccctggtgaaccgcatcgagctgaagggcatcgacttcaaggaggacggcaacatcctggggcacaagctggagtacaactacaacagccacaacgtctatatcatggccgacaagcagaagaacggcatcaaggtgaacttcaagatccgccacaacatcgaggacggcagcgtgcagctcgccgaccactaccagcagaacacccccatcggcgacggccccgtgctgctgcccgacaaccactacctgagctaccagtccgccctgagcaaagaccccaacgagaagcgcgatcacatggtcctgctggagttcgtgaccgccgccgggatcactctcggcatggacgagctgtacaagtaataa
    }
}
