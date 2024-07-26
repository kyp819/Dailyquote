import { Collection, Schema, model } from 'mongoose';

// Movie Interface - defines the structure of a movie document
export interface IMovie 
{
    id: number;
    quote: string;
    author: string;
   
}   

// Movie Schema - defines the structure of a movie document
let movieSchema = new Schema<IMovie>
({
    id: Number,
    quote: String,
    author: String
});

let Movie = model<IMovie>('Movie', movieSchema);
console.log(Movie)
export default Movie;