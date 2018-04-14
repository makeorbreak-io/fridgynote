var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const db = require('./database/db');

var notesRouter = require('./routes/notes');

var app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

db.connect();
//db.populate();

app.use('/notes', notesRouter);

module.exports = app;
