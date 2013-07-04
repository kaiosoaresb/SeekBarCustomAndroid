/*
 * SeekBarCustom.java
 *
 * 03/07/2013 - 10:06:41
 *
 * Copyright: Kaio Soares
 */

package com.appsis.animobyandroid.view.customcomponents;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.widget.SeekBar;

/**
 * Esta classe é responsável por Extender a um {@link SeekBar} mas modificar a Cor. Foram modificados os seguintes métodos: setProgressDrawable e setThumb.
 * 
 * @author KaioSoares
 */
public class SeekBarCustom extends SeekBar
{
	// ===================================================================================
	// ==================================== ATRIBUTOS ====================================
	// ===================================================================================
	
	/**
	 * Esta constante armazena a Cor do FUNDO
	 */
	private static final int COR_FUNDO = 0xFF939393;
	/**
	 * Esta constante armazena a Cor da FRENTE (Progresso Atual e o Direcional)
	 */
	private static final int COR_FRENTE = 0xFF32B4E5;
	
	// ===================================================================================
	// =================================== CONSTRUTORES ==================================
	// ===================================================================================
	
	/**
	 * Este contrutor receberá o Contexto e chamará o método validate para modificar a aparência do {@link SeekBar}
	 * 
	 * @param context - {@link Activity} responsável
	 */
	public SeekBarCustom(Context context)
	{
		super(context);
		
		// inicia uma Thread, pois neste momento o SeekBar ainda não está em tela, e seu tamanho ainda não foi definido. Sendo assim, na hora de desenhar pode haver problemas, pois
		// o desenho leva em consideração a largura e altura do SeekBar.
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// enquanto não tiver um tamanho definido irá ficar neste loop.
				while (getWidth() == 0)
				{
					try
					{
						// espera um tempo para tentar novamente.
						Thread.sleep(50);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				// executa na Thread Principal o Validate
				post(new Runnable()
				{
					@Override
					public void run()
					{
						validate();
					}
				});
			}
		});
		
		// starta a Thread
		thread.start();
	}
	
	// ===================================================================================
	// ===================================== METODOS =====================================
	// ===================================================================================
	
	/**
	 * Este método será responsável por criar fundo do {@link SeekBar}, para que fique padronizado em todos os Tablets.
	 */
	private void validate()
	{
		// declara o Paint da Barra aqui fora para que não fique sendo criado toda hora um novo objeto do Tipo Paint, pois isto consome muita memória.
		final Paint paintBarra = new Paint();
		paintBarra.setAntiAlias(true);
		
		// já define alguns valores para poder desenhar a barra e o direcional. É definido fora do método de desenho para que o mesmo não fique realizando cálculos a cada iteração
		// de redesenhar.
		final float circunferenciaMaior = 12;
		final float circunferenciaMenor = (int) (circunferenciaMaior * 0.4f);
		final int largura = (int) (getWidth() - (2 * (circunferenciaMaior * 1.5f)));
		final int altura = 3;
		final int yInicial = (int) ((getHeight() * 0.5f) - (altura * 0.5f));
		final int centroY = (int) (yInicial + (altura * 0.5f));
		
		// seta o Padding para ter um espaçamento da Esquerda.
		setPadding((int) (circunferenciaMaior * 1.5f), 0, 0, 0);
		
		// seta o Drawable da Barra
		setProgressDrawable(new Drawable()
		{
			@Override
			public void setColorFilter(ColorFilter cf)
			{
			}
			
			@Override
			public void setAlpha(int alpha)
			{
			}
			
			@Override
			public int getOpacity()
			{
				return 0;
			}
			
			@Override
			public void draw(Canvas canvas)
			{
				// seta a cor do Fundo para Desenhar a Barra total
				paintBarra.setColor(COR_FUNDO);
				paintBarra.setStyle(Style.FILL);
				
				// desenha o Fundo todo
				canvas.drawRect(0, yInicial, largura, yInicial + altura, paintBarra);
				
				// atualiza a cor do Paint para a cor do Progresso
				paintBarra.setColor(COR_FRENTE);
				
				// faz o calculo para saber qual progresso equivale a 1 no SeekBar
				float widthSingleProgress = (float) largura / (float) getMax();
				
				// desenha o Progresso
				canvas.drawRect(0, yInicial, widthSingleProgress * getProgress(), yInicial + altura, paintBarra);
			}
		});
		
		// cria o Paint da Bolinha que será desenhada em cima da Barra
		final Paint paintBolinha = new Paint();
		paintBolinha.setAntiAlias(true);
		// define a Espessura do Paint
		paintBolinha.setStrokeWidth(circunferenciaMaior * 0.2f);
		
		// seta o Drawable do Direcional (Bolinha)
		setThumb(new Drawable()
		{
			@Override
			public void setColorFilter(ColorFilter cf)
			{
			}
			
			@Override
			public void setAlpha(int alpha)
			{
			}
			
			@Override
			public int getOpacity()
			{
				return 0;
			}
			
			@Override
			public void draw(Canvas canvas)
			{
				// seta a cor do Paint para desenhar a Bolinha Maior com Opacidade
				paintBolinha.setColor(COR_FRENTE);
				// seta a opacidade
				paintBolinha.setAlpha(100);
				paintBolinha.setStyle(Style.FILL);
				
				// faz o calculo para saber qual progresso equivale a 1 no SeekBar
				float widthSingleProgress = (float) largura / (float) getMax();
				
				// através do widthSingleProgress saberá onde deverá ficar o centro da Bolinha
				canvas.drawCircle((widthSingleProgress * getProgress()), centroY, circunferenciaMaior, paintBolinha);
				
				// Agora irá desenhar a bolinha menor, então atualiza a opacidade para 100%
				paintBolinha.setAlpha(255);
				
				// desenha a bolinha menor
				canvas.drawCircle((widthSingleProgress * getProgress()), centroY, circunferenciaMenor, paintBolinha);
				
				// modifica o tipo de Preenchimento para STROKE para desenhar a Bolinha Maior, mas sem preenchimento.
				paintBolinha.setStyle(Style.STROKE);
				canvas.drawCircle((widthSingleProgress * getProgress()), centroY, circunferenciaMaior, paintBolinha);
			}
		});
	}
	
	// ===================================================================================
	// ===================================== EVENTOS =====================================
	// ===================================================================================
	
	// ===================================================================================
	// ================================= GETTERS E SETTERS ===============================
	// ===================================================================================
	
}
