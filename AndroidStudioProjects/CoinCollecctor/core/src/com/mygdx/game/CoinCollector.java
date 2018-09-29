package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinCollector extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] collector;
	int collectorState =0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int collectorY;
	Rectangle manRectangle;
	ArrayList<Integer> coinX = new ArrayList<Integer>();
	ArrayList<Integer> coinY = new ArrayList<Integer>();
	ArrayList<Rectangle>coinRectangle = new ArrayList<Rectangle>();
	int coinCount;
	Texture coin;
	Random random;
	ArrayList<Integer> bombX = new ArrayList<Integer>();
	ArrayList<Integer> bombY = new ArrayList<Integer>();
	ArrayList<Rectangle>bombRectangle = new ArrayList<Rectangle>();
	int bombCount;
	Texture bomb;
	int score = 0;
	BitmapFont bitmapFont;
	int gameState = 0;
	Texture dizzy;
	Sound sound;
	Sound sound1;
	Sound sound2;



	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		collector = new Texture[4];
		collector[0] = new Texture("frame-1.png");
		collector[1] = new Texture("frame-2.png");
		collector[2] = new Texture("frame-3.png");
		collector[3] = new Texture("frame-4.png");
		collectorY = Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		random = new Random();
		bomb = new Texture("bomb.png");
		manRectangle = new Rectangle();
		bitmapFont =new BitmapFont();
		bitmapFont.setColor(Color.BLUE);
		bitmapFont.getData().setScale(9);
		dizzy = new Texture("dizzy-1.png");
		sound = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
		sound1 = Gdx.audio.newSound(Gdx.files.internal("gameover.wav"));
		sound2 = Gdx.audio.newSound(Gdx.files.internal("coincollect.wav"));



	}

	public void makeBomb(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		bombX.add(Gdx.graphics.getWidth());
		bombY.add((int) height);
	}

	public void makeCoin(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		coinX.add(Gdx.graphics.getWidth());
		coinY.add((int) height);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState==1){


			if(bombCount<200){
				bombCount++;
			}else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangle.clear();
			for (int i = 0;i<bombX.size();i++){
				batch.draw(bomb,bombX.get(i),bombY.get(i));
				bombX.set(i,bombX.get(i)-12);
				bombRectangle.add(new Rectangle(bombX.get(i),bombY.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			if(coinCount<100){
				coinCount++;
			}else {
				coinCount = 0;
				makeCoin();
			}
			coinRectangle.clear();
			for (int i = 0;i<coinX.size();i++){
				batch.draw(coin,coinX.get(i),coinY.get(i));
				coinX.set(i,coinX.get(i)-12);
				coinRectangle.add(new Rectangle(coinX.get(i),coinY.get(i),coin.getWidth(),coin.getHeight()));
			}

			if(Gdx.input.justTouched()){
				sound.play(1.0f);
				velocity=-10;
			}
			if(pause<4){
				pause++;
			}else{
				pause = 0;
				if(collectorState<3){
					collectorState++;
				}else {
					collectorState =0;
				}}
			velocity+=gravity;
			collectorY-=velocity;
			if(collectorY<0){
				collectorY =0;
			}


		}
		else if(gameState==0){

			if(Gdx.input.justTouched()){
				gameState = 1;
			}

		}else if(gameState==2){

			if(Gdx.input.justTouched()){
                sound1.stop();
				gameState = 1;
				collectorY = Gdx.graphics.getHeight()/2;
				velocity = 0;
				score = 0;
				coinX.clear();
				coinY.clear();
				coinRectangle.clear();
				coinCount = 0;
				bombRectangle.clear();
				bombX.clear();
				bombY.clear();
				bombCount = 0;

			}

		}

		if(gameState==2){
           batch.draw(dizzy,Gdx.graphics.getWidth()/2-collector[collectorState].getWidth()/2,collectorY);

		}else{
			batch.draw(collector[collectorState],Gdx.graphics.getWidth()/2-collector[collectorState].getWidth()/2,collectorY);
		}



		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2-collector[collectorState].getWidth()/2,collectorY,collector[collectorState].getWidth(),collector[collectorState].getHeight());

		for(int i =0;i<coinRectangle.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangle.get(i))){
				score++;
				Gdx.app.log("Coins","Collison ep2irhb 2k;n;1");
				coinRectangle.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				sound2.play(1.0f);
				break;
			}
		}

		for(int i =0;i<bombRectangle.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangle.get(i))){
				Gdx.app.log("Bombs","Bombs ep2irhb 2k;n;1");
				gameState = 2;
				sound1.play(1.0f);
			}
		}

		bitmapFont.draw(batch,String.valueOf(score),100,200);
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
