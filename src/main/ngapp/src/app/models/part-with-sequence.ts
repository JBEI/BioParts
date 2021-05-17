import {Sequence} from "./Sequence";
import {Part} from "./part";
import {PartSource} from "./part-source";

export class PartWithSequence {
    part: Part;
    sequence: Sequence;
    partSource: PartSource;
    children: PartWithSequence[];
}
