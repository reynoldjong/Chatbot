import React from 'react';
import classes from './RatingTable.module.css';
import RatingRow from './RatingRow/RatingRow';
/**
 * Compoenent representing a table of all the files in the  database
 * @param {props} props:
 *  @param {list} feedback - a list containing every file in Files.db
 */
const ratingTable = (props) => {

  return (
    <React.Fragment>
    <div className={classes.ScrollableTable}>
      <table className={classes.fixedHeader + " striped"}>
        <thead className={"z-depth-1"}>
          <tr>
            <th>Answer</th>
            <th>Good Rating</th>
            <th>Bad Rating</th>
          </tr>
        </thead>
        <tbody>
          {props.answerRating.map((item, i) => {
            const message= item['answer'];
            const good = item['good'];
            const bad = item['bad'];
            return <RatingRow key={i}  message={message} good={good} bad={bad}/>
          }

          )}

        </tbody>
      </table>
    </div>
    </React.Fragment>
  );
}

export default ratingTable;