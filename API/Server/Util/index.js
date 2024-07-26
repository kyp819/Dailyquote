"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.SanitizeArray = void 0;
function SanitizeArray(inputString) {
    if (Array.isArray(inputString)) {
        return inputString.map((value) => value.trim());
    }
    else if (typeof inputString === 'string') {
        return inputString.split(",").map((value) => value.trim());
    }
    else {
        console.error("Invalid input type");
        return [];
    }
}
exports.SanitizeArray = SanitizeArray;
//# sourceMappingURL=index.js.map