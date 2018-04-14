var express = require('express');
var router = express.Router();
const db = require('../database/db')
const { check,validationResult } = require('express-validator/check');

var multer  = require('multer')
var Storage = multer.diskStorage({
    destination: function(req, file, callback) {
        callback(null, "uploads/");
    },
    filename: function(req, file, callback) {
        callback(null, file.fieldname + "_" + Date.now() + "_" + file.originalname);
    }
});
var upload = multer({
   storage: Storage
}); //Field name and max count


router.get('/me',check('Authorization').exists(),function(req, res){
    const errors = validationResult(req);
    
    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
      }

      db.getNoteByUser(req.get('Authorization'))
      .then(result => res.json(result))
      .catch((err) => res.status(400).end())

})


router.post('/item', [check('body').exists(), check('listId').exists(), check('Authorization').exists(), check('tagId').exists()], upload.array('avatar'),function (req, res, next) {
    const errors = validationResult(req);

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

router.post('/text', [check('title').exists(), check('body').exists(), check('Authorization').exists(), check('labels').exists(),  check('images').exists(), upload.array('images')], function (req, res, next) {
   
    /*  const errors = validationResult(req);

    if (!errors.isEmpty()) {
        return res.status(422).json({ errors: errors.mapped() });
      }*/

    db.createNewTextNote(req.body.title, req.body.body, req.files.map(a => a.path), req.get('Authorization'), req.body.shared, req.body.labels).then((listItem) => {
            console.log(listItem)
            res.json(listItem)
        })
        .catch((err) => {
            console.log(err)
            res.status(400).end()
        });
});

module.exports = router;
