var express = require('express');
var router = express.Router();
const db = require('../database/db')
const { check,validationResult } = require('express-validator/check');

router.post('/item', [check('body').exists(), check('listId').exists(), check('Authorization').exists(), check('tagId').exists()], function (req, res, next) {
    const errors = validationResult(req);

    console.log(errors);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
      }
    
    db.createNewListItem(req.body.body, req.body.listId, req.get('Authorization'), req.body.shared, req.tagId)
        .then((listItem) => {
            console.log(listItem)
            res.json(listItem)
        })
        .catch((err) => {
            console.log(err)
            res.status(400).end()
        })
});

router.post('/list', function (req, res, next) {
    res.send('index');
});

router.post('/text', function (req, res, next) {
    res.send('index');
});

module.exports = router;
