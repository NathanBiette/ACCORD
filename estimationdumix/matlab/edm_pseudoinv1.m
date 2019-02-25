function edm_pseudoinv1(n, delta, save_result, print, nb_essai)
nbinstru = 4;

% utilise un methode de pseudo-inverse par découpage en matrices carrees

% n : longueur de l'echantillon pour la TF
% delta : pas entre chaque echantillon pour faire le diagramme TF
% save_result : si egal à 1, les donnees sont sauvegardees dans un fichier dans le dossier results
% sinon rien
% print : si egal à 1, le resultat est affiche dans un graphique

if save_result == 1
  load('../results/compteurs.mat');
end;

[xbatterie, xmelodie, xbasse, xaccord, xglobal, N] = load_audio(nb_essai, n, delta, 1);

% matrice contenant les TF de chaque instrument dans chaque colonne
% Cette matrice depend du temps (on avance colonne par colonne dans les
% differents diagrammes temps-frequance)
W = zeros([n nbinstru]);
compteur = 0;

coefs = zeros([4 length(xglobal)]);

leng = floor((N-n)/delta);

tic;
for j=1:leng
    % on met la TF du i-eme instrument a l'instant j dans
    % la i-eme colonne de W
    W(:,1) = xmelodie(:,j);
    W(:,2) = xaccord(:,j);
    W(:,3) = xbasse(:,j);
    W(:,4) = xbatterie(:,j);

    % c sera le nombre d'instruments effectivement
    % en train de jouer
    c = 4;

    % on enregistre dans liste, les indices des
    % instruments qui ne jouent pas
    % pour que notre probleme ait une solution
    % (car il faut que les blocs de W soient inversibles)
    liste = [];
    for k=1:nbinstru
        if W(:,k) == zeros([n 1])
            c = c - 1;
        else
            liste = horzcat(liste, [k]);
        end;
    end;

    % On construit Wprim a partir de ces
    % instruments qui jouent (il y en a c)
    Wprim = zeros([n c]);

    for k=1:c
        Wprim(:,k) = W(:,liste(k));
    end;

    % on va tronquer la matrice Wprim et la colonne
    % de xglobal pour qu'on puisse decouper Wprim
    % en plusieurs matrices carres
    m = n - mod(n , c);

    % pre-allocation pour le pseudo-inverse Winv
    Winv = zeros([c m]);

    % colonne tronquee du diagramme temps-frequence
    % de la sortie
    Y = xglobal(1:m, j);

    k = 1;

    % Calcul du pseudo inverse
    % on decoupe Wprim en matrices carres
    % de dimension c*c, le pseudo-inverse
    % Winv sera la concatenation horizontale
    % des matrices inverses des matrices carres
    % constituant Wprim
    q = 0;
    while k+c-1<m
        % on extrait la matrice carree
        B = Wprim(k:k+c-1, :);

        % Si elle n'est pas inversible,
        % on laisse une matrice de zeros
        % sinon on calcule son inverse
        % que l'on place dans Winv
        if (det(B) ~= 0 && max(max(B))<10)
            Winv(:, k:k+c-1) = inv(B)*(c/m);
            q = q+1;
        end;
        k = k + c;
    end;

    Winv = Winv/q;
    
    
    % On calcule Xprim qui contient tous les coefficients
    % de chaque instruments. En effet on avait Y = Wprim*Xprim
    % et Winv �tant le pseudo-inverse de Winv, on a
    % Xprim = Winv * Y
    Xprim = Winv * Y;

    % Xprim ne contient que les instruments qui jouent
    % (et pas ceux qu'on a enlev� un peu plus haut)
    % il faut donc reconstruire X en mettant des zeros
    % pour les instruments qui ne jouent pas
    decalage = 0;
    X = zeros([4 1]);
    for k=1:nbinstru

        % si k est dans liste on retrouve sa valeur dans Xprim
        if any(k==liste)
            X(k) = Xprim(k-decalage);

        % sinon, on met 0
        else
            X(k) = 0;
            decalage = decalage + 1;
        end;
    end;

    % on place X dans le diagramme temporel des coefficients
    coefs(:,j) = X;

end;
temps_traitement = toc;

% on construit l'echelle des abscisses
absc = zeros([1 leng]);

for j=1:length(coefs)
    absc(j) = j;
end;

if save_result==1
  main_code = fileread('edm_pseudoinv1.m');
  info = strcat('../Data/essai',nb_essai,'/info');
  filename = strcat('../results/pseudoinv1_essai',nb_essai,'_',num2str(compteur_pseudoinv1, '%03.0f'),'.mat');
  save(filename, 'main_code', 'info', 'n', 'delta', 'coefs', 'temps_traitement');
  compteur_pseudoinv1 = compteur_pseudoinv1 + 1;
  save('../results/compteurs', 'compteur_pseudoinv2', 'compteur_pseudoinv1', 'compteur_nmf');
end;

if print == 1
  figure;
  plot(absc,coefs(1,:), absc, coefs(2,:), absc, coefs(3,:), absc, coefs(4,:));
end;
end
