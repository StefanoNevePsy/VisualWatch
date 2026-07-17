package it.visualwatch;

import android.app.*;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.os.*;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import java.util.*;

/** A deliberately low-power visual therapy timer: only one small canvas redraws per second. */
public class MainActivity extends Activity {
    private final ArrayList<Preset> presets = new ArrayList<>();
    private Preset selected;
    private ThemeSpec selectedTheme;
    private long endsAt = 0;
    private TimerFace face;
    private TextView time, state, selectedLabel, eyebrow, title;
    private Button primary, pip;
    private LinearLayout root, controls;
    private final ThemeSpec[] themes = {
        new ThemeSpec("Salvia", Color.rgb(16,20,23), Color.rgb(153,244,192), Color.rgb(230,236,233), Color.rgb(42,52,55)),
        new ThemeSpec("Oceano", Color.rgb(10,20,31), Color.rgb(100,210,255), Color.rgb(225,241,250), Color.rgb(31,58,74)),
        new ThemeSpec("Tramonto", Color.rgb(35,18,17), Color.rgb(255,177,118), Color.rgb(255,238,228), Color.rgb(77,45,39)),
        new ThemeSpec("Lavanda", Color.rgb(25,20,38), Color.rgb(204,177,255), Color.rgb(242,237,255), Color.rgb(56,47,78)),
        new ThemeSpec("Mono", Color.rgb(18,18,18), Color.WHITE, Color.WHITE, Color.rgb(65,65,65))
    };
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() { public void run() { render(); handler.postDelayed(this, 1000); } };

    static class Preset { String name; int minutes, warning; Preset(String n, int m, int w) { name=n; minutes=m; warning=w; } }
    static class ThemeSpec { String name; int background, accent, text, track; ThemeSpec(String n,int b,int a,int t,int r){name=n;background=b;accent=a;text=t;track=r;} }

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved); load(); if (presets.isEmpty()) { presets.add(new Preset("Terapia breve", 30, 5)); presets.add(new Preset("Sessione lunga", 60, 10)); } selected=presets.get(0); int themeIndex=getPreferences(0).getInt("theme",0); selectedTheme=themes[Math.max(0,Math.min(themeIndex,themes.length-1))];
        build(); handler.post(ticker);
    }
    private int dp(int n) { return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,n,getResources().getDisplayMetrics()); }
    private TextView text(String s, int size) { TextView v=new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(Color.rgb(230,236,233)); return v; }
    private void build() {
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(24),dp(20),dp(24),dp(16));
        eyebrow=text("VISUAL WATCH",12); eyebrow.setLetterSpacing(.18f); root.addView(eyebrow);
        title=text("Il tuo tempo, visibile.",27); title.setTypeface(Typeface.DEFAULT_BOLD); root.addView(title);
        state=text("Pronto quando lo sei",15); state.setTextColor(Color.rgb(174,188,181)); LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2); sp.setMargins(0,dp(6),0,dp(10)); root.addView(state,sp);
        face=new TimerFace(this); root.addView(face,new LinearLayout.LayoutParams(-1,0,1));
        time=text("00:00",46); time.setGravity(Gravity.CENTER); time.setTypeface(Typeface.create("sans",Typeface.BOLD)); root.addView(time);
        selectedLabel=text("",15); selectedLabel.setGravity(Gravity.CENTER); selectedLabel.setTextColor(Color.rgb(174,188,181)); root.addView(selectedLabel);
        controls=new LinearLayout(this); controls.setGravity(Gravity.CENTER); controls.setPadding(0,dp(14),0,0);
        Button choose=button("PRESET"); choose.setOnClickListener(v->choosePreset()); controls.addView(choose,new LinearLayout.LayoutParams(0,dp(48),1));
        Button theme=button("TEMA"); LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,dp(48),1); tp.setMargins(dp(8),0,0,0); theme.setOnClickListener(v->chooseTheme()); controls.addView(theme,tp);
        primary=button("INIZIA"); LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,dp(48),1); pp.setMargins(dp(8),0,0,0); controls.addView(primary,pp); primary.setOnClickListener(v->toggle()); root.addView(controls);
        pip=button("MINI TIMER"); LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,dp(42)); ip.setMargins(0,dp(10),0,0); root.addView(pip,ip); pip.setOnClickListener(v->enterMini());
        setContentView(root); applyTheme(); render();
    }
    private Button button(String label) { Button b=new Button(this); b.setText(label); b.setTextSize(12); b.setTypeface(Typeface.DEFAULT_BOLD); return b; }
    private void applyTheme() { ThemeSpec t=selectedTheme; root.setBackgroundColor(t.background); getWindow().setStatusBarColor(t.background); getWindow().setNavigationBarColor(t.background); eyebrow.setTextColor(t.accent); title.setTextColor(t.text); time.setTextColor(t.text); state.setTextColor(t.text); selectedLabel.setTextColor(t.text); for(int i=0;i<controls.getChildCount();i++){ Button b=(Button)controls.getChildAt(i); b.setTextColor(t.background); b.setBackgroundTintList(ColorStateList.valueOf(t.accent)); } pip.setTextColor(t.background); pip.setBackgroundTintList(ColorStateList.valueOf(t.accent)); face.setTheme(t); }
    private void render() {
        long now=System.currentTimeMillis(); long left=Math.max(0, endsAt-now); boolean running=endsAt>now;
        if (endsAt>0 && !running) { endsAt=0; getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); state.setText("Sessione conclusa · ottimo lavoro"); }
        int total=selected.minutes*60, remaining=(int)Math.ceil(left/1000.0); int shown=running?remaining:total;
        time.setText(String.format(Locale.ITALY,"%02d:%02d",shown/60,shown%60));
        boolean near=running && remaining<=selected.warning*60;
        face.setProgress(running?(float)remaining/total:1f,near,running);
        selectedLabel.setText(selected.name+" · "+selected.minutes+" min · avviso a "+selected.warning+" min");
        if(running) state.setText(near?"Si avvicina la fine":"Timer attivo · schermo acceso");
        primary.setText(running?"FERMA":"INIZIA"); pip.setVisibility(running?View.VISIBLE:View.GONE);
    }
    private void toggle() { if(endsAt>System.currentTimeMillis()) { endsAt=0; getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); } else { endsAt=System.currentTimeMillis()+selected.minutes*60000L; getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); } render(); }
    private void enterMini() { if(Build.VERSION.SDK_INT>=26) enterPictureInPictureMode(new PictureInPictureParams.Builder().setAspectRatio(new android.util.Rational(1,1)).build()); }
    private void choosePreset() {
        String[] names=new String[presets.size()+1]; for(int i=0;i<presets.size();i++) names[i]=presets.get(i).name+" · "+presets.get(i).minutes+" min"; names[names.length-1]="＋ Crea preset";
        new AlertDialog.Builder(this).setTitle("Scegli una terapia").setItems(names,(d,w)-> { if(w==presets.size()) editPreset(); else { selected=presets.get(w); render(); }}).show();
    }
    private void chooseTheme() { String[] names=new String[themes.length]; for(int i=0;i<themes.length;i++) names[i]=themes[i].name; new AlertDialog.Builder(this).setTitle("Tema del timer").setSingleChoiceItems(names,Arrays.asList(themes).indexOf(selectedTheme),(d,w)-> { selectedTheme=themes[w]; getPreferences(0).edit().putInt("theme",w).apply(); applyTheme(); d.dismiss(); }).show(); }
    private void editPreset() {
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); int p=dp(22); box.setPadding(p,0,p,0);
        EditText n=new EditText(this); n.setHint("Nome (es. Aerosol)"); EditText m=new EditText(this); m.setHint("Durata in minuti"); m.setInputType(2); EditText w=new EditText(this); w.setHint("Avvisa a quanti minuti dalla fine"); w.setInputType(2); box.addView(n);box.addView(m);box.addView(w);
        new AlertDialog.Builder(this).setTitle("Nuovo preset").setView(box).setNegativeButton("Annulla",null).setPositiveButton("Salva",(d,x)-> { try { Preset q=new Preset(n.getText().toString().trim(),Integer.parseInt(m.getText().toString()),Integer.parseInt(w.getText().toString())); if(q.name.isEmpty()||q.minutes<1||q.warning<0||q.warning>=q.minutes) throw new Exception(); presets.add(q); selected=q; save(); render(); } catch(Exception e) { Toast.makeText(this,"Controlla i valori del preset",Toast.LENGTH_LONG).show(); }}).show();
    }
    private void load() { String raw=getPreferences(0).getString("presets",""); for(String r:raw.split("\\n")) { String[] x=r.split("\\|",-1); try { if(x.length==3) presets.add(new Preset(x[0],Integer.parseInt(x[1]),Integer.parseInt(x[2]))); }catch(Exception ignored){} } }
    private void save() { StringBuilder s=new StringBuilder(); for(Preset p:presets) s.append(p.name.replace("|","").replace("\n","")).append('|').append(p.minutes).append('|').append(p.warning).append('\n'); getPreferences(0).edit().putString("presets",s.toString()).apply(); }
    @Override protected void onDestroy(){ handler.removeCallbacks(ticker); super.onDestroy(); }
    @Override public void onPictureInPictureModeChanged(boolean inPip, android.content.res.Configuration config) { super.onPictureInPictureModeChanged(inPip,config); eyebrow.setVisibility(inPip?View.GONE:View.VISIBLE); title.setVisibility(inPip?View.GONE:View.VISIBLE); state.setVisibility(inPip?View.GONE:View.VISIBLE); selectedLabel.setVisibility(inPip?View.GONE:View.VISIBLE); controls.setVisibility(inPip?View.GONE:View.VISIBLE); pip.setVisibility(inPip?View.GONE:(endsAt>System.currentTimeMillis()?View.VISIBLE:View.GONE)); root.setPadding(inPip?dp(6):dp(24),inPip?dp(6):dp(20),inPip?dp(6):dp(24),inPip?dp(6):dp(16)); face.setGlass(inPip); }

    static class TimerFace extends View { Paint p=new Paint(1); float progress=1; boolean warning,active,glass; ThemeSpec theme; TimerFace(Context c){super(c);p.setStrokeCap(Paint.Cap.ROUND);} void setProgress(float v,boolean w,boolean a){progress=v;warning=w;active=a;invalidate();} void setTheme(ThemeSpec t){theme=t;invalidate();} void setGlass(boolean v){glass=v;invalidate();}
        protected void onDraw(Canvas c){ super.onDraw(c); float d=Math.min(getWidth(),getHeight())*.72f, x=getWidth()/2f,y=getHeight()/2f; if(theme==null)return; if(glass){p.setStyle(Paint.Style.FILL);p.setColor(Color.argb(165,theme.track>>16&255,theme.track>>8&255,theme.track&255));c.drawRoundRect(dp(4),dp(4),getWidth()-dp(4),getHeight()-dp(4),d*.14f,d*.14f,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(1));p.setColor(Color.argb(120,255,255,255));c.drawRoundRect(dp(4),dp(4),getWidth()-dp(4),getHeight()-dp(4),d*.14f,d*.14f,p);} p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(d*.09f);p.setColor(theme.track);c.drawCircle(x,y,d/2,p);p.setColor(warning?Color.rgb(255,190,104):theme.accent);c.drawArc(x-d/2,y-d/2,x+d/2,y+d/2,-90,360*progress,false,p);p.setStyle(Paint.Style.FILL);p.setColor(active?theme.accent:theme.track);c.drawCircle(x,y,d*.08f,p); } private float dp(int n){return n*getResources().getDisplayMetrics().density;}
    }
}
