const mongoose = require('mongoose');
const fs = require('fs');
const Schema = mongoose.Schema;

function connect() {
    const connectionString = fs.readFileSync('./database/config.txt').toString();
    console.log(connectionString)
    mongoose.connect(connectionString,{dbName:'fridgynote'})
        .then(() => {
            console.log('Database Connected')
        })
        .catch((err) => console.log(err))
}

const userTagSchema = new Schema({
    userId: String,
    tagId: String
}, { _id: false})

const textNoteSchema = new Schema({
    title: String,
    body: String,
    images: [{ path: String }],
    owner: userTagSchema,
    shared: [userTagSchema],
    labels: []
}, { versionKey: false })

const listNoteSchema = new Schema({
    title: String,
    items: [String],
    owner: userTagSchema,
    shared: [userTagSchema]
}, { versionKey: false })

const listItemSchema = new Schema({
    body: String,
    listId: Schema.Types.ObjectId,
    owner: String,
    shared: [String],
    tagId: String
}, { versionKey: false })

const TextNote = mongoose.model('text_note', textNoteSchema);
const ListNote = mongoose.model('list_note', listNoteSchema);
const ListItem = mongoose.model('list_item', listItemSchema);


function populate() {
    TextNote.remove({}, function (err) {
        if (err) return console.log(err);
    });

    ListNote.remove({}, function (err) {
        if (err) return console.log(err);
    });
    ListItem.remove({}, function (err) {
        if (err) return console.log(err);
    });

    TextNote.create({
        title: "List Title",
        body: "Body title",
        owner: {
            userId: "moura",
            tagId: "fridgynote1"
        },
        shared: [
            {
                userId: "lago",
                tagId: "fridgynote1"
            }
        ],
        labels: ["Fun", "Family", "Work", "Chores"]
    }, function (err, textNote) {
        if (err) return console.log(err);
    });

    ListNote.create({
        title: "Shopping List",
        items: ["Cereals", "Carrots", "Gasoline"],
        owner: {
            userId: "ines",
            tagId: "fridgynote2"
        },
        shared: [
            {
                userId: "moura",
                tagId: "fridgynote2"
            },
            {
                userId: "lago",
                tagId: "fridgynote2"
            },
            {
                userId: "tostas",
                tagId: "fridgynote2"
            }
        ]
    }, function (err, listNote) {
        if (err) return console.log(err);

        ListItem.create({
            body: "Cereals",
            listId: listNote.id,
            owner: "tostas",
            shared: ["ines", "lago", "moura"],
            tagId: "fridgynote3"
        })


    })



}

function getNoteByUser(userId){

    var promise1 = TextNote.find( { $or: [ { 'owner.userId': userId }, { shared: {$elemMatch: {userId: userId }}}]});

    var promise2 = ListNote.find({ $or: [ {'owner.userId': userId }, { shared: {$elemMatch: {userId: userId }}}]});

    var promise3 = ListItem.find({ $or: [ { owner : userId}, { shared : userId}]});

    Promise.all([promise1, promise2, promise3]).then(function(values) {

        const textNote = {'key': 'textNote', 'values': JSON.stringify(values[0])};
        const listNote = {'key': 'listNote', 'values': JSON.stringify(values[1])};
        const listItem = {'key': 'listItem', 'values': JSON.stringify(values[2])};
        
        return [textNote, listNote, listItem];
      });
};

getNoteByUser('moura');


module.exports = { connect, populate }

