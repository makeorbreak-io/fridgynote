var express = require('express');
var router = express.Router();
const db = require('../database/db')
const { check, validationResult,oneOf } = require('express-validator/check');

router.get('/me', check('Authorization').exists(), function (req, res) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.getNoteByUser(req.get('Authorization'))
        .then(result => res.json(result))
        .catch((err) => res.status(400).end())

})

router.post('/item', [check('body').exists(), check('listId').exists(), check('Authorization').exists(), check('tagId').exists()], function (req, res, next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.createNewListItem(req.body.body, req.body.listId, req.get('Authorization'), req.body.shared, req.body.tagId)
        .then((listItem) => {
            res.json(listItem)
        })
        .catch((err) => {
            console.log(err)
            res.status(400).end()
        })
});

router.post('/processItem', [check('tagId').exists(), check('Authorization').exists()], function (req, res, next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.processListItem()
        .then(result => res.json(result))
        .catch((err) => res.status(400).end())

});

router.post('/list', [check('title').exists(), check('tagId').exists(), check('Authorization').exists()], function (req, res, next) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.createNewListNote(req.body.title, req.body.items, req.get('Authorization'), req.body.tagId, req.body.shared)
        .then((listNote) => {
            res.json(listNote)
        })
        .catch((err) => {
            res.status(400).end()
        })
});

router.post('/text', function (req, res, next) {
    res.send('index');
});

router.put('/list/:id', [check('Authorization').exists(),oneOf([check('title').exists(), check('items').exists(), check('shared').exists()])], function (req, res) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.editListNote(req.params.id, req.body.title, req.body.items, req.body.shared)
        .then((listNote) => {
            res.json(listNote)
        })
        .catch((err) => {
            console.log(err)
            res.status(400).end()
        })
});

router.delete('/item/:tagId', [check('Authorization').exists()], function (req, res) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.deleteListItem(req.get('Authorization'), req.params.tagId)
        .then(() => {
            res.status(200).end();
        })
        .catch((err) => {
            console.log(err)
            res.status(400).end();
        })
});

router.delete('/list/:id', [check('Authorization').exists()], function (req, res) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.deleteListNote(req.params.id)
        .then(() => {
            res.status(200).end();
        })
        .catch((err) => {
            console.log(err)
            res.status(400).end();
        })
});

router.delete('/text/:id', [check('Authorization').exists()], function (req, res) {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
    }

    db.deleteTextNote(req.params.id)
        .then(() => {
            res.status(200).end();
        })
        .catch((err) => {
            console.log(err)
            res.status(400).end();
        })
});


module.exports = router;


// oneOf([check('title').exists(), check('items').exists(), check('shared').exists()])