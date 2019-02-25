function edm_pseudoinv2(n, delta, save_result, print, nb_essai)
nbinstru = 4;

% utilise un methode de pseudo-inverse avec le pseudo-inverse (W*transpose(W))^(-1)*W

% n : longueur de l'echantillon pour la TF
% delta : pas entre chaque echantillon pour faire le diagramme TF
% save_result : si egal à 1, les donnees sont sauvegardees dans un fichier dans le dossier results
% sinon rien
% print : si egal à 1, le resultat est affiche dans un graphique


if save_result == 1
  load('../results/compteurs.mat');
end;

[xbatterie, xmelodie, xbasse, xaccompagnement, xglobal, N] = load_audio(nb_essai, n, delta, 1);


W = zeros([n nbinstru]);
compteur = 0;

leng = floor((N-n)/delta);

coefs = zeros([4 leng]);
deter = zeros([1 leng]);

tic;
for j=1:leng
    % on met la TF du i-eme instrument � l'instant j dans
    % la i-eme colonne de W
    W(:,1) = xmelodie(:,j);
    W(:,2) = xaccompagnement(:,j);
    W(:,3) = xbasse(:,j);
    W(:,4) = xbatterie(:,j);

    % c sera le nombre d'instruments effectivement
    % en train de jouer
    c = 4;

    % on enregistre dans liste, les indices des
    % instruments qui ne jouent pas
    % pour que notre probl�me ait une solution
    % (car il faut que les blocs de W soient inversibles)
    liste = [];
    for k=1:nbinstru
        if W(:,k) == zeros([n 1])
            c = c - 1;
        else
            liste = horzcat(liste, [k]);
        end;
    end;

    % On construit Wprim � partir de ces
    % instruments qui jouent (il y en a c)
    Wprim = zeros([n c]);

    for k=1:c
        Wprim(:,k) = W(:,liste(k));
    end;

    %le raisonnement ne fonctionne que si la matrice B est inversible
    %or, c'est une matrice de Gram construite � partir des vecteurs
    %We(i) avec les e(i) �l�ments de la base canonique, cela revient
    %� supposer que les colonnes de W sont ind�pendantes, ce qui
    %a de tr�s fortes chances d'�tre vrai (sinon, infinit� de solution)
    B = transpose(Wprim)*Wprim;

    deter(j)=det(B);

    % On v�rifie tout de m�me que B est inversible avant de faire le calcul
    % L'annulation du gradient de la divergence li�e au moindre carr� donne
    % que X = B^{-1} *(traspose(Wprim) * Y)
    if det(B) ~= 0
        compteur = compteur + 1;
        Y = xglobal(:,j);
        Xprim = B\(transpose(Wprim)*Y);
        decalage = 0;

        % on reconstruit le vecteur X des coefficients
        % en rajoutant les instruments qui ne jouent pas
        X = zeros([4 1]);

        for k=1:nbinstru
            if any(k==liste)
                X(k) = Xprim(k-decalage);
            else
                X(k) = 0;
                decalage = decalage + 1;
            end;
        end;
        coefs(:,j) = X;

    end;


end;
temps_traitement = toc;

% on construit les abscisses
absc = zeros([1 length(coefs)]);

for j=1:length(coefs)
    absc(j) = j;
end;

moy = zeros([nbinstru length(coefs)]);
for k=1:nbinstru-1
    p = 50;
    moy(k,:) = filter(ones(1,p)/p,1,coefs(k,:));
end;

p = 500;
moy(4,:) = filter(ones(1,p)/p,1,coefs(4,:));

if save_result==1
  main_code = fileread('edm_pseudoinv2.m');
  info = strcat('../Data/essai',nb_essai,'/info');
  filename = strcat('../results/pseudoinv2_essai',nb_essai,'_',num2str(compteur_pseudoinv2, '%03.0f'),'.mat');
  save(filename, 'main_code', 'info', 'n', 'delta', 'coefs', 'temps_traitement');
  compteur_pseudoinv2 = compteur_pseudoinv2 + 1;
  save('../results/compteurs', 'compteur_pseudoinv2', 'compteur_pseudoinv1', 'compteur_nmf');
end;

if print == 1
  figure;
  plot(absc,coefs(1,:), absc, coefs(2,:), absc, coefs(3,:), absc, coefs(4,:));
end;
end
