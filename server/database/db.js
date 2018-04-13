const mongoose = require('mongoose');

function connect() {
    mongoose.connect('mongodb://jadmin:pwd1231pwd@jarvis-iot.ml:27017/fridgynote')
        .then(() => console.log('Database Connected'))
        .catch((err) => console.log(err))
}


module.exports = { connect }