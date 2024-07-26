"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const mongoose_1 = require("mongoose");
let movieSchema = new mongoose_1.Schema({
    id: Number,
    quote: String,
    author: String
});
let Movie = (0, mongoose_1.model)('Movie', movieSchema);
console.log(Movie);
exports.default = Movie;
//# sourceMappingURL=movie.js.map