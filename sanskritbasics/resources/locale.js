
var LANG={};

var lang=localStorage.getItem('lang');
if (!lang) { lang='en'; }

LANG['en']={
  'LANG_LANG'           : 'Language:',
  'LANG_ABOUT'          : 'More Apps & About',
  'LANG_CONGR'          : 'Congratulatons!',
  'LANG_SCORE_TXT'      : 'Your score:',
  'LANG_AGAIN'          : 'Try again',
  'LANG_MOVES'          : 'wrong answers: ',
  'LANG_RIGHT'          : 'right',
  'LANG_WRONG'          : 'wrong',
  'LANG_NOW'            : 'now',
  'LANG_TOTAL'          : 'total',
  'LANG_RESET'          : 'reset',
  'LANG_MENU'           : 'Menu',
  'LANG_SCORE'          : 'score',
  'LANG_VOWELS'         : 'vowels',
  'LANG_VOWELS_REV'     : 'vowels inverted',
  'LANG_CONSONANTS'     : 'consonants',
  'LANG_CONSONANTS_REV' : 'consonants inverted',
  'LANG_SCORES'         : 'Score table'
}

LANG['ru']={
  'LANG_LANG'           : 'Language:',
  'LANG_ABOUT'          : 'О программе и другие приложения',
  'LANG_CONGR'          : 'Поздравляю!',
  'LANG_SCORE_TXT'      : 'Ваш результат:',
  'LANG_AGAIN'          : 'Играть еще',
  'LANG_MOVES'          : 'неправильных: ',
  'LANG_RIGHT'          : 'правильные',
  'LANG_WRONG'          : 'неправильные',
  'LANG_NOW'            : 'сейчас',
  'LANG_TOTAL'          : 'всего',
  'LANG_RESET'          : 'сброс',
  'LANG_MENU'           : 'Меню',
  'LANG_SCORE'          : 'счет',
  'LANG_VOWELS'         : 'гласные',
  'LANG_VOWELS_REV'     : 'гласные наоборот',
  'LANG_CONSONANTS'     : 'согласные',
  'LANG_CONSONANTS_REV' : 'согласные наоборот',
  'LANG_SCORES'         : 'Таблица рекордов'
}



function localize()
{
  lang=localStorage.getItem('lang');
  if (!lang) { lang='en'; }
  for (var st in LANG[lang])
  {
    display_value(st, LANG[lang][st]);
  }
}

function display_value(id, val)
{
  var cmt=document.getElementById(id);
  if (cmt)
  {
    if ( cmt.hasChildNodes() ) { while ( cmt.childNodes.length >= 1 ) { cmt.removeChild( cmt.firstChild ); } }
    var node=document.createTextNode(val);
    cmt.appendChild(node);
  }
}
