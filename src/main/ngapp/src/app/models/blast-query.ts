export class BlastQuery {
    blastProgram: string;
    sequence: string;

    constructor() {
        this.blastProgram = 'BLAST_N';
        this.sequence = '';
    }
}
