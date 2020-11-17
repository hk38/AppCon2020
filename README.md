# ドアプレート（令和最新版）
柔軟に表示を変更可能なデジタルドアプレートのクライアントアプリ

## MainActivity
表示送信画面，テンプレート編集画面，設定画面をBottomNavigationで表示するActivity．
表示しているFragmentに対応したFloatingActionButtonの動作も担当．

### HomeFragment
Raspberry Piに対して送信を行うFragment．
テンプレートの選択と，メインタイトル・サブタイトルの編集が可能．
FAB長押しで表示の時間指定送信が可能．

### ListFragment
テンプレートを一覧表示するFragment．
既存テンプレートの編集と新規テンプレートの作成が可能．

### SettingFragment
FCMトークンの表示やRaspberry Piの動体検知感度設定を行うFramgent．
トークン表示時には警告のDialogを表示．
感度設定はSeekbarを使用．

## EditActivity
テンプレートの編集を行うActivity．
