export class Sequence {
    id: number;
    icePartId: string;
    partSource: string;
    bpLength: number;
    genbankStartBP: number;
    endBP: number;
    name: string;
    features = [];
    iceEntryURI?: string;
    sequence: string;
    identifier: string;
    date: string;

    constructor() {
        this.features = [];
    }
}
