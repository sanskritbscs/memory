
var LANG={};

var lang=localStorage.getItem('lang');
if (!lang) { lang='en'; }

LANG['en']={
  'LANG_SCORE_TITLE'    : 'Your scores',
  'LANG_DIFF'           : 'Field size:',
  'LANG_SCORES'         : 'Score table',
  'LANG_ANNOTATE'       : 'Annotate:',
  'LANG_START'          : 'Start!',
  'LANG_ABOUT'          : 'More Apps & About',
  'LANG_CONGR'          : 'Congratulatons!',
  'LANG_SCORE_TXT'      : 'Your score:',
  'LANG_AGAIN'          : 'Try again',
  'LANG_MOVES'          : 'moves: ',
  'LANG_LANG'           : 'Language:',
  'LANG_MENU'           : 'Menu',
  'LANG_SMALL'          : 'Small',
  'LANG_MEDIUM'         : 'Normal',
  'LANG_BIG'            : 'Big',
  'LANG_ANNYES'         : 'Yes',
  'LANG_ANNNO'          : 'No',
  'LANG_VARIANT'        : 'Variant:',
  'LANG_VARIANT_SIMPLE' : 'Simple',
  'LANG_VARIANT_HARD'   : 'Hard'
}

LANG['ru']={
  'LANG_SCORE_TITLE'    : 'Таблица рекордов',
  'LANG_DIFF'           : 'Размер поля:',
  'LANG_SCORES'         : 'Таблица рекордов',
  'LANG_ANNOTATE'       : 'Подписи:',
  'LANG_START'          : 'Старт!',
  'LANG_ABOUT'          : 'О программе и другие приложения',
  'LANG_CONGR'          : 'Поздравляю!',
  'LANG_SCORE_TXT'      : 'Ваш результат:',
  'LANG_AGAIN'          : 'Играть еще',
  'LANG_MOVES'          : 'Количество ходов: ',
  'LANG_LANG'           : 'Language:',
  'LANG_MENU'           : 'Меню',
  'LANG_SMALL'          : 'Маленький',
  'LANG_MEDIUM'         : 'Нормальный',
  'LANG_BIG'            : 'Большой',
  'LANG_ANNYES'         : 'Да',
  'LANG_ANNNO'          : 'Нет',
  'LANG_VARIANT'        : 'Вариант:',
  'LANG_VARIANT_SIMPLE' : 'Просто',
  'LANG_VARIANT_HARD'   : 'Сложно'
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
