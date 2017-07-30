package br.com.jordan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.omg.PortableInterceptor.Interceptor;

import java.awt.Color;
import java.util.Random;

import static com.badlogic.gdx.Input.Keys.V;

public class FlappyBird extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture passaro[], fundo, canoBaixo, canoTopo, gameOver;

    //Atributos de configuração
    private int movimento = 0;
    private float larguraDispositivo = 0;
    private float alturaDispositivo = 0;
    private int estadoJogo = 0;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private int pontuacao = 0;
    private boolean marcouPonto = false;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoBaixo, retanguloCanoTopo;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

    //Variacao para trocar o vetor de imagem do passaro
    private float variacao = 0;

    //Variacao altura dos canos
    private Random random;

    //Tempo da animação de queda do passáro
    private float velocidadeQuedaPassaro = 0;

    //Posicao inicial passaro
    private float posicaoInicialVertical = 0;

    //Posicao do cano no topo
    private float posicaoMovimentoCanoHorizontal = 0;

    //Espaço entre os canos
    private float espacoEntreCanos = 0;

    private float deltaTime = 0;

    //Altura randomica
    private float alturaEntreCanosRandomica = 0;


    @Override
    public void create() {
        batch = new SpriteBatch();

        random = new Random();

        fonte = new BitmapFont();
        fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        mensagem.getData().setScale(3);

        passaroCirculo = new Circle();

        passaro = new Texture[3];
        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");

        gameOver = new Texture("game_over.png");

        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;
        posicaoInicialVertical = alturaDispositivo / 2;

        posicaoMovimentoCanoHorizontal = larguraDispositivo;

        espacoEntreCanos = 300;
    }

    @Override
    public void render() {

        camera.update();

        //Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();

        //Variacao do vetor de imagens dos passaros
        variacao += deltaTime * 10;

        if (variacao > 2) {
            variacao = 0;
        }

        // Se o jogo nao estiver iniciado
        if (estadoJogo == 0) {
            //Verificando se a tela foi tocada, inicia-se o jogo
            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }
        } else {
            //Velocidade da queda do passaro
            velocidadeQuedaPassaro++;

            if (posicaoInicialVertical > 0 || velocidadeQuedaPassaro < 0) {
                posicaoInicialVertical -= velocidadeQuedaPassaro;
            }


            if (estadoJogo == 1) {
                posicaoMovimentoCanoHorizontal -= deltaTime * 200;

                if (Gdx.input.justTouched()) {
                    velocidadeQuedaPassaro = -15;
                }

                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = random.nextInt(400) - 200;
                    marcouPonto = false;
                }

                if (posicaoMovimentoCanoHorizontal < 120) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }

            } else {
                if(Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQuedaPassaro = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                }
            }

        }
        //Configurando os dados da projeção da camera
        batch.setProjectionMatrix(camera.combined);


        //Iniciando a exibição das imagens
        batch.begin();

        //Plano de Fundo
        //Textura, eixo x, eixo Y, largura imagem, altura imagem
        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);

        //Cano Topo
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        //Cano baixo
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

        //Desenhando as texturas
        //Textura, eixo x, eixo Y
        batch.draw(passaro[(int) variacao], 120, posicaoInicialVertical);

        //Desenhando a pontucao
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if(estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            mensagem.draw(batch, "Toque para reiniciar!", larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        // Fechando a exibição das imagens
        batch.end();

        passaroCirculo.set(120 + passaro[0].getWidth() / 2, posicaoInicialVertical + passaro[0].getHeight() / 2, passaro[0].getWidth() / 2);
        retanguloCanoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoBaixo.getWidth(), canoBaixo.getHeight()
        );
        retanguloCanoTopo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoTopo.getWidth(), canoTopo.getHeight()
        );

        //Teste de colisao
        if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo) || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo) {
            estadoJogo = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
