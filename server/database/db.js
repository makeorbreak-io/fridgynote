const mongoose = require('mongoose');
const fs = require('fs');
const Schema = mongoose.Schema;

function connect() {
    const connectionString = fs.readFileSync('./database/config.txt').toString();
    console.log(connectionString)
    mongoose.connect(connectionString, { dbName: 'fridgynote' })
        .then(() => {
            console.log('Database Connected')
        })
        .catch((err) => console.log(err))
}

const userTagSchema = new Schema({
    userId: String,
    tagId: String
}, { _id: false })

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

function createNewListItem(body, listId, owner, shared, tagId) {
    const item = new ListItem({
        body: body,
        listId: listId,
        owner: owner,
        tagId: tagId,
        shared: shared
    })
    return item.save()
}

function createNewTextNote(title, body, images, owner, shared, labels) {
    const textNote = new ListItem({
        title: title,
        body: body,
        images: images,
        owner: owner,
        labels: labels
    })
    return textNote.save()
}

function getNoteByUser(userId) {

    var promise1 = TextNote.find({ $or: [{ 'owner.userId': userId }, { shared: { $elemMatch: { userId: userId } } }] });

    var promise2 = ListNote.find({ $or: [{ 'owner.userId': userId }, { shared: { $elemMatch: { userId: userId } } }] });

    var promise3 = ListItem.find({ $or: [{ owner: userId }, { shared: userId }] });

    return Promise.all([promise1, promise2, promise3]).then(function (values) {
        const joined = {
            'textNote': values[0],
            'listNote': values[1], 
            'listItem': values[2],
        }

        return Promise.resolve(joined);
    });
};

function updateTextNote(id, title, body, images, owner, shared, labels) {

    return TextNote.findById({ _id: id })
    .then((textNote) => {
        if (title) textNote.title = title;
        if (body) textNote.body = body;
        if (images) textNote.images = images;
        if (shared) textNote.shared = shared;
        if (labels) textNote.labels = labels;
        return textNote.save();
    })

}

module.exports = { connect, populate, createNewListItem, getNoteByUser, createNewTextNote, updateTextNote }

