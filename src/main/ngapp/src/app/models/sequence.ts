export class Sequence {
    id: number;
    icePartId: string;
    partSource: string;
    bpLength: number;
    genbankStartBP: number;
    endBP: number;
    name: string;
    features: any[];
    iceEntryURI?: string;
    sequence: string;

    constructor() {
        this.features = [];
    }
}
