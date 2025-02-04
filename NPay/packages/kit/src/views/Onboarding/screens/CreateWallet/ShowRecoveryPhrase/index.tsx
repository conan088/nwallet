import { useCallback } from 'react';

import { useNavigation } from '@react-navigation/core';
import { useRoute } from '@react-navigation/native';
import { useIntl } from 'react-intl';

import Layout from '../../../Layout';
import { EOnboardingRoutes } from '../../../routes/enums';
import PhraseSheet from '../PhraseSheet';

import type { IOnboardingRoutesParams } from '../../../routes/types';
import type { RouteProp } from '@react-navigation/native';
import type { StackNavigationProp } from '@react-navigation/stack';

type NavigationProps = StackNavigationProp<
  IOnboardingRoutesParams,
  EOnboardingRoutes.ShowRecoveryPhrase
>;
type RouteProps = RouteProp<
  IOnboardingRoutesParams,
  EOnboardingRoutes.ShowRecoveryPhrase
>;

const ShowRecoveryPhrase = () => {
  const intl = useIntl();
  const navigation = useNavigation<NavigationProps>();
  const route = useRoute<RouteProps>();
  const { mnemonic, fromVerifyPassword, walletId, networkId } = route.params;
  const onPressSavedPhrase = useCallback(() => {
    if (fromVerifyPassword) {
      navigation.goBack();
    }
    else {
      navigation.replace(EOnboardingRoutes.BehindTheScene, route.params);
    }

  }, [navigation, route.params, fromVerifyPassword]);

  return (
    <Layout
      title={intl.formatMessage({ id: 'content__click_below_to_copy' })}
      description={intl.formatMessage({
        id: 'modal__for_your_eyes_only_desc',
      })}
      fullHeight
      secondaryContent={
        <PhraseSheet
          mnemonic={mnemonic}
          walletId={walletId as string}
          networkId={networkId as string}
          onPressSavedPhrase={onPressSavedPhrase}
        />
      }
    />
  );
};

export default ShowRecoveryPhrase;
