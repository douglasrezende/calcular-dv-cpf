package com.douglas.gerar.cpf;

import java.awt.Frame;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.douglas.gerar.cpf.properties.PropertiesCPF;
/*
 * 
 *  
 *  No calculo do CPF, o DV módulo 11 corresponde ao resto da divisão por 11 do somatório 
 *  da multiplicação de cada algarismo da base respectivamente por 9, 8, 7, 6, 5, 4, 3, 2, 1 e 0,
 *   a partir da unidade.
O resto 10 é considerado 0. Veja, abaixo, exemplo de cálculo de DV módulo 11 para o CPF nº 280012389:

2    8   0   0   1    2    3    8    9
x    x   x   x   x    x    x    x    x
1    2   3   4   5    6    7    8    9
-   --   -   -   -   --   --   --   --
2 + 16 + 0 + 0 + 5 + 12 + 21 + 64 + 81 = 201 ÷ 11 = 18, com resto 3

2   8   0   0   1    2    3    8    9    3
x   x   x   x   x    x    x    x    x    x
0   1   2   3   4    5    6    7    8    9
-   -   -   -   -   --   --   --   --   --
0 + 8 + 0 + 0 + 4 + 10 + 18 + 56 + 72 + 27 = 195 ÷ 11 = 17, com resto 8

Portanto, CPF+DV = 280.012.389-38

A Região Fiscal onde emitido o CPF (definida pelo nono dígito) tem a seguinte abrangência:

   0 (RS)                            4 (AL PB PE RN)             7 (ES RJ)                                               
   1 (DF GO MS MT TO)                5 (BA SE)                   8 (SP)          
   2 (AC AM AP PA RO RR)             6 (MG)                      9 (PR SC) 
   3 (CE MA PI)

   CPF+DV = 280.012.389-38

 * 
 */

public class CPF extends Frame {

	private static final long serialVersionUID = 1L;
	private TextField txtCPF;
	private TextField txtDV;
	private Button btnCalcular;
	private Button btnReiniciar;

	private static final int TAMANHO_CPF_SEM_DV = 9; 
	private static final int TAMANHO_CPF_COM_PRIMEIRO_DV = 10; 


	public CPF() {
		super("Calcular DV do CPF");
		super.setResizable(false);
		setLayout(new FlowLayout());
		//super.setSize(500, 800);

		Label lblCPF = new Label("CPF (9 dígitos):");
		txtCPF = new TextField(10);  // 9 dígitos + 1 espaço visual
		txtCPF.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar()) || txtCPF.getText().length() >= 9) {
					e.consume();
				}
			}
		});

		Label lblDV = new Label("DV (2 dígitos):");
		txtDV = new TextField(3);  // 2 dígitos + 1 espaço visual
		txtDV.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar()) || txtDV.getText().length() >= 2) {
					e.consume();
				}
			}
		});

		btnCalcular = new Button("Calcular");
		btnCalcular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean temErro = false;
				List<Long> resultadosMultiplicacao = new ArrayList<>();
				String cpf = txtCPF.getText();
				try {

					if (cpf == null
							|| cpf.isEmpty()
							|| cpf.length() < 9) {

						throw new Exception("Favor informar 9 digitos do CPF!!!!!");

					}

				} catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "ATENÇÃO: " + ex.getMessage());
					temErro = true;
					txtCPF.setText("");
					txtDV.setText("");
				}

				//String dv = txtDV.getText();
				//System.out.println("CPF: " + cpf + ", DV: " + dv);

				if (!temErro) {
					//Calculo DV modulo 11
					try {
						//280012389 CPF+DV = 280.012.389-38

						calcularDV(resultadosMultiplicacao, cpf);

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

			private void calcularDV(List<Long> resultadosMultiplicacao, String cpf) throws Exception {
				String primeiroDV = calcularPrimeiroDV(resultadosMultiplicacao, cpf);
				String cpfComPrimeiroDV = cpf + primeiroDV;
				resultadosMultiplicacao = new ArrayList<>();
				String segundoDV = calcularSegundoDV(resultadosMultiplicacao, cpfComPrimeiroDV);
				txtDV.setText(primeiroDV+segundoDV);
			}
		});

		btnReiniciar = new Button("Reiniciar");
		btnReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtCPF.setText("");
				txtDV.setText("");
			}
		});

		add(lblCPF);
		add(txtCPF);
		add(lblDV);
		add(txtDV);
		add(btnCalcular);
		add(btnReiniciar);

		setSize(400, 140);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
	}

	public static void main(String[] args) {
		new CPF();
	}

	public static String calcularPrimeiroDV(List<Long> resultadosMultiplicacao, String cpf) throws Exception {

		Long valor = 0L;
		Long peso = 0L;
		String pesoStr = null;
		String digitoCPFStr = null;
		int posInicial = 0;
		int posFinal = 1;
		int chavePeso = 0;

		for (int i = 0 ; i <= 9; i++) {

			if (posFinal > TAMANHO_CPF_SEM_DV ) {

				break;

			}

			digitoCPFStr = cpf.substring(posInicial, posFinal);

			valor = digitoCPFStr != null ? Long.valueOf(digitoCPFStr) : null;

			pesoStr = PropertiesCPF.getProperty(String.valueOf(chavePeso), "peso_primeiro_dv.properties");
			peso = pesoStr != null ? Long.valueOf(pesoStr) : null;

			if (valor != null
					&& peso != null) {


				resultadosMultiplicacao.add(valor * peso);


			}


			posInicial +=1;
			posFinal +=1;
			chavePeso +=1;

		}

		Long soma = 0L;
		for (Long resultado : resultadosMultiplicacao) {
			soma += Long.valueOf(resultado);
		}

		int restoDivisao = soma.intValue() % 11;

		if (restoDivisao == 10) {
			return String.valueOf(0);
		}

		return String.valueOf(restoDivisao);
	}

	public static String calcularSegundoDV(List<Long> resultadosMultiplicacao, String cpf) throws Exception {

		Long valor = 0L;
		Long peso = 0L;
		String pesoStr = null;
		String digitoCPFStr = null;
		int posInicial = 0;
		int posFinal = 1;
		int chavePeso = 0;

		for (int i = 0 ; i <= 10; i++) {

			if (posFinal > TAMANHO_CPF_COM_PRIMEIRO_DV ) {

				break;

			}

			digitoCPFStr = cpf.substring(posInicial, posFinal);

			valor = digitoCPFStr != null ? Long.valueOf(digitoCPFStr) : null;

			pesoStr = PropertiesCPF.getProperty(String.valueOf(chavePeso), "peso_segundo_dv.properties");
			peso = pesoStr != null ? Long.valueOf(pesoStr) : null;

			if (valor != null
					&& peso != null) {


				resultadosMultiplicacao.add(valor * peso);


			}


			posInicial +=1;
			posFinal +=1;
			chavePeso +=1;

		}

		Long soma = 0L;
		for (Long resultado : resultadosMultiplicacao) {
			soma += Long.valueOf(resultado);
		}

		int restoDivisao = soma.intValue() % 11;


		if (restoDivisao == 10) {
			return String.valueOf(0);
		}

		return String.valueOf(restoDivisao);
	}
}
