import { Request, Response, NextFunction } from 'express';

import Movie from '../Models/movie';
import { SanitizeArray } from '../Util';

/**
 * This function displays the movie list in JSON format
 *
 * @export
 * @param {Request} req
 * @param {Response} res
 * @param {NextFunction} next
 */
export function DisplayQuotes(req: Request, res: Response, next: NextFunction): void
{
    Movie.find({})
    .then((data) =>
    {
        res.status(200).json({success: true, msg: "Movie List Retrieved and Displayed", data: data, token: null})
        
    })
    .catch((err) =>
    {
        console.error(err);
    })
}

/**
 * This function displays a single movie by ID in JSON format
 *
 * @export
 * @param {Request} req
 * @param {Response} res
 * @param {NextFunction} next
 */
export function DisplayMovieById(req: Request, res: Response, next: NextFunction) : void
{
    // endpoint should be /api/:id
    let id = req.params.id;

    // ensure that the id is valid
    if (id.length != 24)
    {
        res.status(400).json({success: false, msg: "A valid ID is required to retrieve a movie", data: null, token: null});
    }
    else
    {
        Movie.findById({_id: id})
        .then((data) =>
        {
            if(data)
            {
                res.status(200).json({success: true, msg: "One Movie Retrieved and Displayed", data: data, token: null})
            }
            else
            {
                res.status(404).json({success: false, msg: "Movie not found", data: null, token: null});
            }
        })
        .catch((err) =>
        {
            console.error(err);
        })
    }
}
