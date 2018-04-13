const mongoose = require('mongoose');
const fs = require('fs')

function connect() {
    const connectionString = fs.readFileSync('./database/config.txt').toString();
    console.log(connectionString)
    mongoose.connect(connectionString)
        .then(() => console.log('Database Connected'))
        .catch((err) => console.log(err))
}


module.exports = { connect }

